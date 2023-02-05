package org.essentialss.implementation.world.points.warps;

import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

public class SWarpsImpl implements SWarp {

    private final @NotNull SWorldData worldData;
    private final @NotNull Vector3d position;
    private final @NotNull String warpName;

    public SWarpsImpl(@NotNull SWarpBuilder builder, @NotNull SWorldData data) {
        this.worldData = data;
        this.position = new Validator<>(builder.point()).notNull().validate();
        this.warpName = new Validator<>(builder.name()).notNull().validate();
    }

    @Override
    public @NotNull String identifier() {
        return this.warpName;
    }

    @Override
    public @NotNull SWorldData worldData() {
        return this.worldData;
    }

    @Override
    public @NotNull Vector3d position() {
        return this.position;
    }
}
