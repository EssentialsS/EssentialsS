package org.essentialss.implementation.config.value.simple;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ComponentConfigValue implements SingleConfigValue<Component> {

    private final @NotNull Object[] nodes;

    public ComponentConfigValue(@NotNull Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable Component parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        String value = node.getString();
        if (null == value) {
            return null;
        }
        return GsonComponentSerializer.gson().deserialize(value);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable Component value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }
        node.set(GsonComponentSerializer.gson().serialize(value));
    }
}
