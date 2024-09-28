package org.essentialss.config.value.modifiers.rely;

import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class NullableRelyOnConfigValue<T, O> extends AbstractRelyOnConfigValue<T, O> implements SingleConfigValue<T> {

    public NullableRelyOnConfigValue(@NotNull ConfigValue<T> configValue, @NotNull ConfigValue<O> relyOn, @NotNull Predicate<O> condition) {
        super(configValue, relyOn, condition);
    }

    @Override
    protected @Nullable T onFail() {
        return null;
    }

    public static <T> NullableRelyOnConfigValue<T, Boolean> ifFalse(ConfigValue<T> configValue, ConfigValue<Boolean> rely) {
        return new NullableRelyOnConfigValue<>(configValue, rely, bool -> !bool);
    }

    public static <T> NullableRelyOnConfigValue<T, Boolean> ifTrue(ConfigValue<T> configValue, ConfigValue<Boolean> rely) {
        return new NullableRelyOnConfigValue<>(configValue, rely, bool -> bool);
    }
}
