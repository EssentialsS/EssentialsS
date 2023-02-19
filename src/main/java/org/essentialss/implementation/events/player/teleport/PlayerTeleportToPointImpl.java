package org.essentialss.implementation.events.player.teleport;

import org.essentialss.api.events.player.teleport.PlayerTeleportToPointEvent;
import org.essentialss.api.world.points.SPoint;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.Location;

public class PlayerTeleportToPointImpl implements PlayerTeleportToPointEvent {

    private final @NotNull Player player;
    private final @NotNull SPoint point;
    private boolean isCancelled;
    private final @NotNull Cause cause;

    public PlayerTeleportToPointImpl(@NotNull Player player, @NotNull SPoint point, @NotNull Cause cause) {
        this.cause = cause;
        this.player = player;
        this.point = point;
    }

    @Override
    public @NotNull Player player() {
        return this.player;
    }

    @Override
    public @NotNull Location<?, ?> currentLocation() {
        return this.player.location();
    }

    @Override
    public @NotNull SPoint point() {
        return this.point;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }
}
