package org.essentialss.implementation.config.value.modifiers;

import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class SingleDefaultConfigValueWrapper<V> implements SingleConfigValue.Default<V> {

    private final @NotNull ConfigValue<V> configValue;
    private final @NotNull V defaultValue;

    public SingleDefaultConfigValueWrapper(@NotNull ConfigValue<V> configValue, @NotNull V value) {
        this.configValue = configValue;
        this.defaultValue = value;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.configValue.nodes();
    }

    @Override
    public @Nullable V parse(@NotNull ConfigurationNode root) throws SerializationException {
        //noinspection allow-nullable
        return this.configValue.parse(root);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable V value) throws SerializationException {
        this.configValue.set(root, value);
    }

    @Override
    public V defaultValue() {
        return this.defaultValue;
    }
}
