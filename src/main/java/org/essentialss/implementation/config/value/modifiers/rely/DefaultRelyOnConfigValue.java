package org.essentialss.implementation.config.value.modifiers.rely;

import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.DefaultConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class DefaultRelyOnConfigValue<T, O> extends AbstractRelyOnConfigValue<T, O> implements SingleConfigValue.Default<T> {

    private final Supplier<T> defaultValue;

    protected DefaultRelyOnConfigValue(@NotNull ConfigValue<T> configValue,
                                       @NotNull ConfigValue<O> relyOn,
                                       @NotNull Predicate<O> condition,
                                       Supplier<T> value) {
        super(configValue, relyOn, condition);
        this.defaultValue = value;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue.get();
    }

    @Override
    protected @Nullable T onFail() {
        return this.defaultValue.get();
    }

    public static <T> DefaultRelyOnConfigValue<T, Boolean> ifFalse(ConfigValue<T> configValue, ConfigValue<Boolean> rely, Supplier<T> onFail) {
        return new DefaultRelyOnConfigValue<>(configValue, rely, bool -> !bool, onFail);
    }

    public static <T> DefaultRelyOnConfigValue<T, Boolean> ifFalse(DefaultConfigValue<T> configValue, ConfigValue<Boolean> rely) {
        return ifFalse(configValue, rely, configValue::defaultValue);
    }

    public static <T> DefaultRelyOnConfigValue<T, Boolean> ifTrue(ConfigValue<T> configValue, ConfigValue<Boolean> rely, Supplier<T> onFail) {
        return new DefaultRelyOnConfigValue<>(configValue, rely, bool -> bool, onFail);
    }

    public static <T> DefaultRelyOnConfigValue<T, Boolean> ifTrue(DefaultConfigValue<T> configValue, ConfigValue<Boolean> rely) {
        return ifTrue(configValue, rely, configValue::defaultValue);
    }
}
