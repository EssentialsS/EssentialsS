package org.essentialss.implementation.command;

import net.kyori.adventure.text.Component;
import org.essentialss.api.ban.BanMultiplayerScreenOptions;
import org.essentialss.api.utils.parameter.ParameterAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.parameter.Parameter;

import java.time.Duration;
import java.util.function.Supplier;

public enum ParameterAdapters implements ParameterAdapter {
    BOOLEAN(Parameter::bool, boolean.class, Boolean.class),
    INTEGER(Parameter::integerNumber, int.class, Integer.class),
    DURATION(Parameter::duration, Duration.class),
    LEGACY_COMPONENT(Parameter::formattingCodeTextOfRemainingElements, Component.class),
    BAN_MULTIPLAYER_SCREEN_OPTIONS(() -> Parameter.enumValue(BanMultiplayerScreenOptions.class), BanMultiplayerScreenOptions.class);
    private final Supplier<Parameter.Value.Builder<?>> builder;
    private final Class<?>[] acceptable;

    @Deprecated
    ParameterAdapters(@NotNull Supplier<Parameter.Value.Builder<?>> acceptable) {
        throw new RuntimeException("No classes specified");
    }

    ParameterAdapters(@NotNull Supplier<Parameter.Value.Builder<?>> supplier, Class<?>... acceptable) {
        this.builder = supplier;
        this.acceptable = acceptable;
    }

    @Override
    public <T> Parameter.Value.Builder<T> builder() {
        return (Parameter.Value.Builder<T>) this.builder.get();
    }

    @Override
    public @NotNull Class<?>[] types() {
        return this.acceptable;
    }
}
