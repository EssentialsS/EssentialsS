package org.essentialss.implementation.ban.ip;

import org.essentialss.api.ban.data.IPBanData;
import org.essentialss.api.config.SerializablePart;
import org.essentialss.implementation.ban.BanLoaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Optional;

public class IPBanDataImpl implements IPBanData {

    private final @NotNull String ipAddress;
    private final @Nullable String lastKnownUsername;
    private final @Nullable LocalDateTime bannedUntil;

    public IPBanDataImpl(@NotNull String ipAddress,
                         @Nullable String lastKnownUsername,
                         @Nullable LocalDateTime bannedUntil) {
        this.ipAddress = ipAddress;
        this.lastKnownUsername = lastKnownUsername;
        this.bannedUntil = bannedUntil;
    }

    @Override
    public Optional<String> lastKnownUsername() {
        return Optional.ofNullable(this.lastKnownUsername);
    }

    @Override
    public Optional<LocalDateTime> bannedUntil() {
        return Optional.ofNullable(this.bannedUntil);
    }

    @Override
    public @NotNull String hostName() {
        return this.ipAddress;
    }

    @Override
    public @NotNull SerializablePart<IPBanData> loader() {
        return BanLoaders.IP_BAN;
    }
}
