package org.essentialss.implementation.config.value;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class SingleDefaultConfigValueImpl<T> implements SingleConfigValue.Default<T> {

    private final @NotNull Object[] nodes;
    private final @NotNull T defaultValue;

    @Deprecated
    public SingleDefaultConfigValueImpl(@NotNull T defaultValue) {
        this(defaultValue, new Object[0]);
    }

    public SingleDefaultConfigValueImpl(@NotNull T defaultValue, @NotNull Object... nodes) {
        this.nodes = nodes;
        this.defaultValue = defaultValue;
    }

    protected abstract void setValue(ConfigurationNode to, @NotNull T value) throws SerializationException;

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable T value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        if (null == value) {
            node.set(null);
            return;
        }
        this.setValue(node, value);
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }
}
