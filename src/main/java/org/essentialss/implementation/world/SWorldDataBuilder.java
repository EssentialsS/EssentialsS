package org.essentialss.implementation.world;

import org.essentialss.api.world.points.SPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings("allow-nullable")
public class SWorldDataBuilder {

    private World<?, ?> world;
    private Collection<SPoint> points = new LinkedHashSet<>();

    public @Nullable World<?, ?> world() {
        return this.world;
    }

    public SWorldDataBuilder setWorld(@NotNull World<?, ?> world) {
        this.world = world;
        return this;
    }

    public @NotNull Collection<SPoint> points() {
        return this.points;
    }

    public SWorldDataBuilder setPoints(@NotNull Collection<SPoint> points) {
        this.points = points;
        return this;
    }
}
