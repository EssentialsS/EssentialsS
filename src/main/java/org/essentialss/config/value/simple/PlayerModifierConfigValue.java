package org.essentialss.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.modifier.SPlayerModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.function.Supplier;

public class PlayerModifierConfigValue implements SingleConfigValue<SPlayerModifier<?>> {

    private final @NotNull Object[] nodes;
    private final @NotNull Supplier<Collection<SPlayerModifier<?>>> suppliers;

    public PlayerModifierConfigValue(Object... nodes) {
        this(SPlayerModifiers::all, nodes);
    }

    public PlayerModifierConfigValue(@NotNull Supplier<Collection<SPlayerModifier<?>>> supplier, Object... nodes) {
        this.suppliers = supplier;
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @Override
    public @NotNull Class<SPlayerModifier<?>> type() {
        return (Class<SPlayerModifier<?>>) (Object) SPlayerModifier.class;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable SPlayerModifier<?> parse(@NotNull ConfigurationNode root) throws SerializationException {
        String value = root.node(this.nodes()).getString();
        if (null == value) {
            return null;
        }
        return this.suppliers.get().stream().filter(modifier -> modifier.key().formatted().equalsIgnoreCase(value)).findAny().orElse(null);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable SPlayerModifier<?> value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }
        node.set(value.key().formatted());
    }
}
