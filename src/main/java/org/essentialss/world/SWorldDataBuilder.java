package org.essentialss.world;

import org.essentialss.api.world.points.SPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.ContextSource;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings("allow-nullable")
public class SWorldDataBuilder {

    private ResourceKey key;
    private String id;
    private Collection<SPoint> points = new LinkedHashSet<>();

    public @Nullable ResourceKey worldKey() {
        return this.key;
    }

    public SWorldDataBuilder setWorldKey(@NotNull ResourceKey world) {
        this.key = world;
        return this;
    }

    public @Nullable String worldId() {
        return this.id;
    }

    public SWorldDataBuilder setWorldId(@NotNull String world) {
        if (Sponge.isServerAvailable()) {
            throw new IllegalArgumentException("Please use setWorldKey when server is ready");
        }
        this.id = world;
        return this;
    }

    public SWorldDataBuilder setWorld(@NotNull ServerWorld world) {
        this.key = world.key();
        return this;
    }

    public SWorldDataBuilder setWorld(@NotNull ContextSource world) {
        if (world instanceof ServerWorld) {
            return this.setWorld((ServerWorld) world);
        }
        this.id = world.context().toString();
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
