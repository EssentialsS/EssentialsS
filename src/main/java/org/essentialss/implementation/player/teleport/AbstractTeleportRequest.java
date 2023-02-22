package org.essentialss.implementation.player.teleport;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.player.teleport.TeleportRequestDirection;
import org.essentialss.api.utils.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.world.Location;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractTeleportRequest implements TeleportRequest {

    private final @NotNull TeleportRequestDirection direction;
    private final @NotNull LocalDateTime sentTime;
    private final @NotNull Function<SGeneralPlayerData, Location<?, ?>> teleportToLocation;
    private final @Nullable Duration expiresAt;

    AbstractTeleportRequest(@NotNull TeleportRequestBuilder builder) {
        this.teleportToLocation = new Validator<>(builder.getTo()).notNull().validate();
        this.direction = new Validator<>(builder.getDirection()).notNull().validate();
        this.sentTime = LocalDateTime.now();
        this.expiresAt = builder.getValidForLength();
    }


    @Override
    public @NotNull TeleportRequestDirection directionOfTeleport() {
        return this.direction;
    }

    @Override
    public @NotNull Function<SGeneralPlayerData, Location<?, ?>> teleportToLocation() {
        return this.teleportToLocation;
    }

    @Override
    public @NotNull LocalDateTime sentTime() {
        return this.sentTime;
    }

    @Override
    public Optional<Duration> validForDuration() {
        return Optional.ofNullable(this.expiresAt);
    }
}
