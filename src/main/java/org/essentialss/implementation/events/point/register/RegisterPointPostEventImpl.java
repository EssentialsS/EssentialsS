package org.essentialss.implementation.events.point.register;

import org.essentialss.api.events.world.points.RegisterPointEvent;
import org.essentialss.api.world.points.SPoint;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cause;

public class RegisterPointPostEventImpl implements RegisterPointEvent.Post {

    private final @NotNull SPoint point;
    private final @NotNull Cause cause;

    public RegisterPointPostEventImpl(@NotNull SPoint point, @NotNull Cause cause) {
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
}
