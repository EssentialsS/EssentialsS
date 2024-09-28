package org.essentialss.command;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import org.essentialss.api.ban.BanMultiplayerScreenOptions;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.modifier.SPlayerModifiers;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.utils.parameter.ParameterAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.registry.RegistryTypes;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum ParameterAdapters implements ParameterAdapter {
    BOOLEAN(Parameter::bool, boolean.class, Boolean.class),
    INTEGER(Parameter::integerNumber, int.class, Integer.class),
    DURATION(Parameter::duration, Duration.class),
    LEGACY_COMPONENT(Parameter::formattingCodeTextOfRemainingElements, Component.class),
    BAN_MULTIPLAYER_SCREEN_OPTIONS(() -> Parameter.enumValue(BanMultiplayerScreenOptions.class), BanMultiplayerScreenOptions.class),
    DAMAGE_TYPE(() -> Parameter.registryElement(TypeToken.get(DamageType.class), RegistryTypes.DAMAGE_TYPE), DamageType.class),
    MODIFIER(() -> Parameter
            .builder(SPlayerModifier.class)
            .completer((context, currentInput) -> SPlayerModifiers
                    .all()
                    .stream()
                    .map(StringIdentifier::identifier)
                    .filter(id -> id.toLowerCase().startsWith(currentInput.toLowerCase()))
                    .map(CommandCompletion::of)
                    .collect(Collectors.toList()))
            .addParser((parameterKey, reader, context) -> {
                String target = reader.parseString();
                return SPlayerModifiers.all().stream().filter(mod -> mod.identifier().equalsIgnoreCase(target)).findAny();
            }), SPlayerModifier.class);

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
