package org.essentialss.implementation.config.value.modifiers;

import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.DefaultConfigValue;
import org.essentialss.implementation.misc.OrElse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RelyOnConfigValue<O, R> implements ConfigValue<O> {

    private final ConfigValue<O> configValue;
    private final Predicate<R> condition;
    private final ConfigValue<R> relyOn;
    private final Supplier<O> value;

    public RelyOnConfigValue(@NotNull ConfigValue<O> configValue, @NotNull ConfigValue<R> relyOn, @NotNull Predicate<R> condition, Supplier<O> value) {
        this.configValue = configValue;
        this.relyOn = relyOn;
        this.value = value;
        this.condition = condition;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.configValue.nodes();
    }

    @Override
    public @Nullable O parse(@NotNull ConfigurationNode root) throws SerializationException {
        @Nullable R relyOnValue = OrElse.ifInstanceMapThrowable(SerializationException.class, this.relyOn, DefaultConfigValue.class,
                                                                (v) -> (R) v.parseDefault(root), (v) -> v.parse(root));
        if (this.condition.test(relyOnValue)) {
            return this.value.get();
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

    public static <O> RelyOnConfigValue<O, Boolean> ifFalse(@NotNull ConfigValue<O> value, ConfigValue<Boolean> relyOn, @NotNull Supplier<O> ifTrue) {
        return new RelyOnConfigValue<>(value, relyOn, (v) -> !v, ifTrue);
    }

    public static <O> RelyOnConfigValue<O, Boolean> ifTrue(@NotNull ConfigValue<O> value, ConfigValue<Boolean> relyOn, @NotNull Supplier<O> ifFalse) {
        return new RelyOnConfigValue<>(value, relyOn, (v) -> v, ifFalse);
    }

}
