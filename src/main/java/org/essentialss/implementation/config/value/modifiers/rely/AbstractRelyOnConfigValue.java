package org.essentialss.implementation.config.value.modifiers.rely;

import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.DefaultConfigValue;
import org.essentialss.implementation.misc.OrElse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.function.Predicate;

public abstract class AbstractRelyOnConfigValue<O, R> implements ConfigValue<O> {

    private final ConfigValue<O> configValue;
    private final Predicate<R> condition;
    private final ConfigValue<R> relyOn;

    protected AbstractRelyOnConfigValue(@NotNull ConfigValue<O> configValue, @NotNull ConfigValue<R> relyOn, @NotNull Predicate<R> condition) {
        this.configValue = configValue;
        this.relyOn = relyOn;
        this.condition = condition;
    }

    public ConfigValue<O> getMainNode() {
        return this.configValue;
    }

    public ConfigValue<R> getRelyingOnNode() {
        return this.relyOn;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.configValue.nodes();
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable O parse(@NotNull ConfigurationNode root) throws SerializationException {
        @Nullable R relyOnValue = OrElse.ifInstanceMapThrowable(SerializationException.class, this.relyOn, DefaultConfigValue.class,
                                                                (v) -> (R) v.parseDefault(root), (v) -> v.parse(root));
        if (this.condition.test(relyOnValue)) {
            return this.onFail();
        }

        return this.configValue.parse(root);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable O value) throws SerializationException {
        this.configValue.set(root, value);
    }

    @Override
    public @NotNull Class<?> type() {
        return this.configValue.type();
    }

    protected abstract @Nullable O onFail();
}
