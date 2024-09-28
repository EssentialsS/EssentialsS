package org.essentialss.ban.account;

import org.essentialss.api.ban.data.AccountBanData;
import org.essentialss.api.config.SerializablePart;
import org.essentialss.ban.BanLoaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.profile.GameProfile;

import java.time.LocalDateTime;
import java.util.Optional;

public class AccountBanImpl implements AccountBanData {

    private final @NotNull GameProfile account;
    private final @Nullable LocalDateTime bannedUntil;

    public AccountBanImpl(@NotNull GameProfile account, @Nullable LocalDateTime bannedUntil) {
        this.account = account;
        this.bannedUntil = bannedUntil;
    }

    @Override
    public GameProfile profile() {
        return this.account;
    }

    @Override
    public Optional<LocalDateTime> bannedUntil() {
        return Optional.ofNullable(this.bannedUntil);
    }

    @Override
    public @NotNull SerializablePart<AccountBanData> loader() {
        return BanLoaders.ACCOUNT_BAN;
    }
}
