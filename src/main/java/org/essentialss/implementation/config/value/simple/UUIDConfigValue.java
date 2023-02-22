package org.essentialss.implementation.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.UUID;

public class UUIDConfigValue implements SingleConfigValue<UUID> {

    private final @NotNull Object[] nodes;

    public UUIDConfigValue(@NotNull Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable UUID parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        String uuidString = node.getString();
        if (null == uuidString) {
            return null;
        }
        return UUID.fromString(uuidString);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable UUID value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        if (null == value) {
            node.set(null);
            return;
        }
        node.set(value.toString());
    }
}
