package org.essentialss.implementation.world.points.spawn;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

import java.util.Collections;

public class DefaultSpawnPoint implements SSpawnPoint {

    private final @NotNull SWorldData worldData;

    public DefaultSpawnPoint(@NotNull SWorldData worldData) {
        this.worldData = worldData;
    }

    @Override
    public @NotNull SWorldData worldData() {
        return this.worldData;
    }

    @Override
    public @NotNull Vector3d position() {
        return this.worldData.spongeWorld().properties().spawnPosition().toDouble();
    }

    @Override
    public UnmodifiableCollection<SSpawnType> types() {
        return new UnmodifiableCollection<>(Collections.singleton(SSpawnType.WORLD_ASSIGNED));
    }
}
