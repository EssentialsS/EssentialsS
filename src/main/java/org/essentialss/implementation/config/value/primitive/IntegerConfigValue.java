package org.essentialss.implementation.config.value.primitive;

import org.essentialss.implementation.config.value.SingleDefaultConfigValueImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class IntegerConfigValue extends SingleDefaultConfigValueImpl<Integer> {
    @Deprecated
    public IntegerConfigValue(int defaultValue) {
        super(defaultValue);
    }

    public IntegerConfigValue(int defaultValue, @NotNull Object... nodes) {
        super(defaultValue, nodes);
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable Integer parse(@NotNull ConfigurationNode root) {
        ConfigurationNode node = root.node(this.nodes());
        if (node.isNull()) {
            return null;
        }
        return node.getInt();
    }

    @Override
    public @NotNull Class<?> type() {
        return int.class;
    }

    @Override
    protected void setValue(ConfigurationNode to, @NotNull Integer value) throws SerializationException {
        to.set(value);
    }
}
