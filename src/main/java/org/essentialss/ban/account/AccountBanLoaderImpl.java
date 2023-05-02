package org.essentialss.ban.account;

import org.essentialss.api.ban.data.AccountBanData;
import org.essentialss.api.config.SerializablePart;
import org.essentialss.config.value.simple.DateTimeConfigValue;
import org.essentialss.config.value.simple.GameProfileConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.LocalDateTime;

public class AccountBanLoaderImpl implements SerializablePart<AccountBanData> {

    private static final GameProfileConfigValue GAME_PROFILE = new GameProfileConfigValue("uuid");
    private static final DateTimeConfigValue DATE_TIME = new DateTimeConfigValue("until");

    @Override
    public void saveTo(@NotNull ConfigurationNode node, @Nullable AccountBanData obj) throws SerializationException {
        GAME_PROFILE.set(node, (null == obj) ? null : obj.profile());
        DATE_TIME.set(node, (null == obj) ? null : obj.bannedUntil().orElse(null));
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable AccountBanImpl loadFrom(@NotNull ConfigurationNode node) throws SerializationException {
        GameProfile gameProfile = GAME_PROFILE.parse(node);
        LocalDateTime dateTime = DATE_TIME.parse(node);
        if (null == gameProfile) {
            return null;
        }
        return new AccountBanImpl(gameProfile, dateTime);
    }
}
