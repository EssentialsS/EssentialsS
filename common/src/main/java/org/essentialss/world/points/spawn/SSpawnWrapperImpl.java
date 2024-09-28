package org.essentialss.world.points.spawn;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collection;

public class SSpawnWrapperImpl implements SSpawnPoint {

    private final @NotNull SWorldData world;
    private final @NotNull Collection<SSpawnType> types;

    public SSpawnWrapperImpl(@NotNull Collection<SSpawnType> types, @NotNull SWorldData data) {
        this.world = data;
        this.types = new Validator<>(new ArrayList<>(types)).notNull().validate();
        this.types.add(SSpawnType.MAIN_SPAWN);

    }

    @Override
    public @NotNull OfflineLocation location() {
        Vector3i position = this.world
                .spongeWorld()
                .map(w -> w.properties().spawnPosition())
                .orElseThrow(() -> new IllegalStateException("World needs to be loaded"));
        return new OfflineLocation(this.world, position.toDouble());
    }

    @Override
    public UnmodifiableCollection<SSpawnType> types() {
        Collection<SSpawnType> types = new ArrayList<>(this.types);
        return new SingleUnmodifiableCollection<>(types);
    }

    @Override
    public SSpawnPointBuilder builder() {
        return new SSpawnPointBuilder()
                .setPosition(() -> this.world
                        .spongeWorld()
                        .map(w -> w.properties().spawnPosition())
                        .orElseThrow(() -> new IllegalStateException("World needs to be loaded"))
                        .toDouble())
                .setSpawnTypes(this.types);
    }
}
