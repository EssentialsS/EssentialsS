package org.essentialss.implementation.world.points.warps;

import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.jetbrains.annotations.NotNull;

public class SWarpsImpl implements SWarp {

    private final @NotNull OfflineLocation location;
    private final @NotNull String warpName;

    public SWarpsImpl(@NotNull SWarpBuilder builder, @NotNull SWorldData data) {
        this.location = data.offlineLocation(new Validator<>(builder.point()).notNull().validate());
        this.warpName = new Validator<>(builder.name()).notNull().validate();
    }

    @Override
    public @NotNull String identifier() {
        return this.warpName;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SWarp)) {
            return false;
        }
        SWarp warp = (SWarp) obj;
        if (!warp.identifier().equals(this.identifier())) {
            return false;
        }
        if (!warp.worldData().identifier().equals(this.worldData().identifier())) {
            return false;
        }
        return warp.identifier().equals(this.identifier());
    }

    @Override
    public String toString() {
        return "Warp(" + this.position() + ", " + this.location.identifier() + ", " + this.identifier() + ")";
    }

    @Override
    public SWarpBuilder builder() {
        return new SWarpBuilder().setName(this.warpName).setPoint(this.location.position());
    }

    @Override
    public @NotNull OfflineLocation location() {
        return this.location;
    }
}
