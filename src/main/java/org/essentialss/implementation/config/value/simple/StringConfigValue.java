package org.essentialss.implementation.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class StringConfigValue implements SingleConfigValue<String> {

    private final @NotNull Object[] nodes;

    public StringConfigValue(Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable String parse(@NotNull ConfigurationNode root) {
        return root.node(this.nodes()).getString();
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable String value) throws SerializationException {
        root.node(this.nodes()).set(value);
    }
}
