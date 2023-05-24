package org.essentialss.command.essentialss.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.api.config.SConfig;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.DefaultConfigValue;
import org.essentialss.api.utils.friendly.FriendlyStrings;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ViewConfigCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull ConfigValue<?> value;
        private final @NotNull SConfig config;

        private Execute(@NotNull SConfig config, @NotNull ConfigValue<?> value) {
            this.value = value;
            this.config = config;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return ViewConfigCommand.execute(context.cause().audience(), this.config, this.value);
        }
    }

    private ViewConfigCommand() {
        throw new RuntimeException("Cannot run");
    }

    public static Command.Parameterized createViewConfigCommand(@NotNull SConfig config) {
        Command.Builder command = Command.builder();
        for (ConfigValue<?> node : config.expectedNodes()) {
            Command.Parameterized sub = Command.builder().executor(new Execute(config, node)).build();
            command.addChild(sub, Arrays.stream(node.nodes()).map(Object::toString).collect(Collectors.joining(".")));
        }
        return command.permission(SPermissions.CONFIG_VIEW_MESSAGE.node()).build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull SConfig config, @NotNull ConfigValue<?> value) {
        Object raw = null;
        if (value instanceof DefaultConfigValue) {
            raw = ((DefaultConfigValue<?>) value).parseDefault(config);
        } else {
            try {
                raw = value.parse(config);
            } catch (SerializationException ignore) {
            }
        }

        Component valueMessage;
        if (null == raw) {
            valueMessage = Component.text("Cannot read value").color(NamedTextColor.RED);
        } else if (raw instanceof Component) {
            valueMessage = (Component) raw;
        } else {
            Object finalRaw = raw;
            if (finalRaw instanceof Collection) {
                Collection<?> collectionValue = (Collection<?>) value;
                Stream<CharSequence> stream = collectionValue.stream().map(v -> {
                    Optional<String> op = FriendlyStrings.ofType(v).map(friendly -> friendly.toFriendlyString(v));
                    //needs to be like this otherwise github actions compiler fails ... for some reason
                    if(op.isPresent()){
                        return op.get();
                    }
                    return v.toString();                });
                String message = stream.collect(Collectors.joining(", "));
                valueMessage = Component.text(message);
            } else {
                valueMessage = Component.text(
                        FriendlyStrings.ofType(finalRaw).map(friendly -> friendly.toFriendlyString(finalRaw)).orElseGet(finalRaw::toString));
            }
        }
        audience.sendMessage(Component
                                     .text(Stream.of(value.nodes()).map(Object::toString).collect(Collectors.joining(".")))
                                     .append(Component.text(" is "))
                                     .append(valueMessage));
        return CommandResult.success();
    }
}
