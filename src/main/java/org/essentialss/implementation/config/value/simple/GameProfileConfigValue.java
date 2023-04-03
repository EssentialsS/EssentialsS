package org.essentialss.implementation.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Optional;
import java.util.UUID;

public class GameProfileConfigValue implements SingleConfigValue<GameProfile> {

    private final @NotNull Object[] nodes;

    public GameProfileConfigValue(@NotNull Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @Override
    public @NotNull Class<GameProfile> type() {
        return GameProfile.class;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable GameProfile parse(@NotNull ConfigurationNode root) throws SerializationException {
        if (!Sponge.isServerAvailable()) {
            return null;
        }
        ConfigurationNode node = root.node(this.nodes());
        String uuidString = node.getString();
        if (null == uuidString) {
            return null;
        }
        UUID uuid = UUID.fromString(uuidString);
        Optional<GameProfile> opProfile = Sponge.server().gameProfileManager().cache().findById(uuid);
        return opProfile.orElse(null);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable GameProfile value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        if (null == value) {
            node.set(null);
            return;
        }
        node.set(value.uuid().toString());
    }
}
