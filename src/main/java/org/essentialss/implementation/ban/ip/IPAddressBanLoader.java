package org.essentialss.implementation.ban.ip;

import org.essentialss.api.ban.data.IPBanData;
import org.essentialss.api.config.SerializablePart;
import org.essentialss.implementation.config.value.simple.DateTimeConfigValue;
import org.essentialss.implementation.config.value.simple.StringConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.LocalDateTime;

public class IPAddressBanLoader implements SerializablePart<IPBanData> {

    private static final @NotNull StringConfigValue IP_ADDRESS = new StringConfigValue("IpAddress");
    private static final @NotNull StringConfigValue LAST_KNOWN_USERNAME = new StringConfigValue("LastKnown");
    private static final @NotNull DateTimeConfigValue BANNED_UNTIL = new DateTimeConfigValue("BannedUntil");

    @SuppressWarnings("DuplicateThrows")
    @Override
    public void saveTo(@NotNull ConfigurationNode node, @Nullable IPBanData obj) throws ConfigurateException, SerializationException {
        if (null == obj) {
            node.set(null);
            return;
        }
        IP_ADDRESS.set(node, obj.hostName());
        LAST_KNOWN_USERNAME.set(node, obj.lastKnownUsername().orElse(null));
        BANNED_UNTIL.set(node, obj.bannedUntil().orElse(null));
    }

    @SuppressWarnings({"allow-nullable", "DuplicateThrows"})
    @Override
    public @Nullable IPBanData loadFrom(@NotNull ConfigurationNode node) throws ConfigurateException, SerializationException {
        String ipAddress = IP_ADDRESS.parse(node);
        if (null == ipAddress) {
            return null;
        }
        String lastKnownUser = LAST_KNOWN_USERNAME.parse(node);
        LocalDateTime bannedUntil = BANNED_UNTIL.parse(node);

        return new IPBanDataImpl(ipAddress, lastKnownUser, bannedUntil);
    }
}
