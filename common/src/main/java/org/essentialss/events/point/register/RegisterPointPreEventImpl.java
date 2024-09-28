package org.essentialss.events.point.register;

import org.essentialss.api.events.world.points.RegisterPointEvent;
import org.essentialss.api.world.points.SPoint;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cause;

public class RegisterPointPreEventImpl implements RegisterPointEvent.Pre {

    private final @NotNull SPoint point;
    private final @NotNull Cause cause;
    private boolean isCancelled;

    public RegisterPointPreEventImpl(@NotNull SPoint point, @NotNull Cause cause) {
        this.point = point;
        this.cause = cause;
    }

    @Override
    public @NotNull SPoint point() {
        return this.point;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
