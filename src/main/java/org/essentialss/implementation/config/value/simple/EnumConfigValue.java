package org.essentialss.implementation.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.EnumSet;

public class EnumConfigValue<E extends Enum<E>> implements SingleConfigValue<E> {

    private final Object[] nodes;
    private final EnumSet<E> values;

    public EnumConfigValue(Class<E> values, Object... nodes) {
        this(EnumSet.allOf(values), nodes);
    }

    public EnumConfigValue(Collection<E> values, Object... nodes) {
        this.nodes = nodes;
        this.values = EnumSet.copyOf(values);
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable E parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        String valueName = node.getString();
        if (null == valueName) {
            return null;
        }
        return this.values.parallelStream().filter(v -> v.name().equalsIgnoreCase(valueName)).findAny().orElse(null);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable E value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        if (null == value) {
            node.set(null);
            return;
        }
        node.set(value.name());
    }

    @Override
    public @NotNull Class<?> type() {
        return this.values.iterator().next().getDeclaringClass();
    }
}
