package org.essentialss.config.value.simple;

import org.essentialss.api.config.value.ConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.registry.DefaultedRegistryType;
import org.spongepowered.api.registry.DefaultedRegistryValue;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.function.Supplier;

public class RegistryConfigValue<R extends DefaultedRegistryValue> implements ConfigValue<R> {

    private final Object[] nodes;
    private final Class<R> type;
    private final Supplier<DefaultedRegistryType<R>> registryType;

    public RegistryConfigValue(Class<R> type, Supplier<DefaultedRegistryType<R>> regType, Object... nodes) {
        this.type = type;
        this.nodes = nodes;
        this.registryType = regType;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable R parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes);
        String idAsString = node.getString();
        if (null == idAsString) {
            return null;
        }
        ResourceKey id = ResourceKey.resolve(idAsString);
        return this.registryType.get().get().findValue(id).orElse(null);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable R value) throws SerializationException {
        if (value == null) {
            root.node(this.nodes).set(null);
            return;
        }
        root.node(this.nodes).set(value.key(this.registryType.get()).formatted());
    }

    @Override
    public @NotNull Class<?> type() {
        return this.type;
    }

    public static RegistryConfigValue<DamageType> damageType(Object... nodes) {
        return new RegistryConfigValue<>(DamageType.class, () -> RegistryTypes.DAMAGE_TYPE, nodes);
    }
}
