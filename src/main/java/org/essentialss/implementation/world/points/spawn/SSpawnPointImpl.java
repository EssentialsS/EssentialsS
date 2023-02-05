package org.essentialss.implementation.world.points.spawn;

import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

public class SSpawnPointImpl implements SSpawnPoint {

    private final @NotNull SWorldData worldData;
    private final @NotNull Vector3d position;
    private final @NotNull SSpawnType type;

    public SSpawnPointImpl(@NotNull SSpawnPointBuilder builder, @NotNull SWorldData data) {
        this.worldData = data;
        this.position = new Validator<>(builder.point()).notNull().validate();
        this.type = new Validator<>(builder.spawnType()).notNull().validate();
    }

    @Override
    public @NotNull SWorldData worldData() {
        return this.worldData;
    }

    @Override
    public @NotNull Vector3d position() {
        return this.position;
    }

    @Override
    public SSpawnType type() {
        return this.type;
    }
}
