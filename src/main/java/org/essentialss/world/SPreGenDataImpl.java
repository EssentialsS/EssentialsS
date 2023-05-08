package org.essentialss.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.utils.CrossSpongePlatformUtils;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.SPreGenData;
import org.essentialss.api.world.SWorldData;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.math.vector.Vector3i;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public class SPreGenDataImpl implements SPreGenData {

    private final Vector3i center;
    private final LinkedTransferQueue<Vector3i> completedChunks = new LinkedTransferQueue<>();
    private final Audience messenger;
    private final double radius;
    private final SWorldData world;
    private boolean hasStarted;

    public SPreGenDataImpl(SWorldData world, Vector3i center, double radius, Audience audience) {
        this.world = world;
        this.radius = radius;
        this.messenger = audience;
        this.center = center;
    }

    void start() throws IllegalStateException {
        if (this.hasStarted) {
            throw new IllegalStateException("PreGen data has already started");
        }
        Optional<CompletableFuture<World<?, ?>>> opFuture = this.world.loadWorld();
        if (!opFuture.isPresent()) {
            throw new IllegalStateException("Cannot load world");
        }
        opFuture.get().thenAccept(world -> CrossSpongePlatformUtils.spongeEngine().scheduler().executor(EssentialsSMain.plugin().container()).execute(() -> {
            this.hasStarted = true;
            this.start(world, this.center);
            this.messenger.sendMessage(Component.text("Completed pregen"));
        }));
    }

    private void start(World<?, ?> world, Vector3i target) {
        List<Vector3i> toProcess = new ArrayList<>();
        List<Vector3i> found = new ArrayList<>(Collections.singletonList(target));
        while (!(toProcess.isEmpty() && found.isEmpty())) {
            this.completedChunks.addAll(toProcess);
            toProcess = found;
            found = new ArrayList<>();
            for (Vector3i process : toProcess) {
                world.loadChunk(process, true);
                this.completedChunks.add(process);
                for (int rangeX = 0; rangeX < 3; rangeX++) {
                    for (int rangeZ = 0; rangeZ < 3; rangeZ++) {
                        Vector3i toAdd = process.add(rangeX - 1, 0, rangeZ - 1);
                        int distance = this.center.distanceSquared(toAdd);
                        if (distance > this.radius) {
                            continue;
                        }
                        found.add(toAdd);
                    }
                }
            }
        }

    }

    @Override
    public SWorldData worldData() {
        return this.world;
    }

    @Override
    public Vector3i center() {
        return this.center;
    }

    @Override
    public double radius() {
        return this.radius;
    }

    @Override
    public UnmodifiableCollection<WorldChunk> completedChunks() {
        Optional<World<?, ?>> opWorld = this.world.spongeWorld();
        if (!opWorld.isPresent()) {
            return new SingleUnmodifiableCollection<>(Collections.emptyList());
        }
        Collection<WorldChunk> chunks = this.completedChunks.stream().map(pos -> opWorld.get().chunk(pos)).collect(Collectors.toList());
        return new SingleUnmodifiableCollection<>(chunks);
    }

    @Override
    public double completedRadius() {
        double[] array = this.completedChunks.stream().mapToDouble(pos -> pos.distance(this.center)).distinct().toArray();
        if (1 > array.length) {
            return 0;
        }
        return array[array.length - 2];
    }
}
