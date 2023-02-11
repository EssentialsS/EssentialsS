package org.essentialss.implementation.config.value.primitive;

import org.essentialss.implementation.config.value.SingleDefaultConfigValueImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class BooleanConfigValue extends SingleDefaultConfigValueImpl<Boolean> {

    @Deprecated
    public BooleanConfigValue(@NotNull Boolean defaultValue) {
        super(defaultValue);
    }

    public BooleanConfigValue(@NotNull Boolean defaultValue, @NotNull Object... nodes) {
        super(defaultValue, nodes);
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable Boolean parse(@NotNull ConfigurationNode root) {
        ConfigurationNode node = root.node(this.nodes());
        if (node.isNull()) {
            return null;
        }
        return node.getBoolean();
    }

    @Override
    protected void setValue(ConfigurationNode to, @NotNull Boolean value) throws SerializationException {
        to.set(value);
    }
}
