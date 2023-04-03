package org.essentialss.implementation.command.essentialss.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.api.config.SConfig;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.utils.friendly.FriendlyStrings;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class SetConfigCommand {

    private static final class Execute<T> implements CommandExecutor {

        private final @NotNull ConfigValue<T> adapter;
        private final @NotNull SConfig config;
        private final @NotNull Parameter.Value<T> newValue;

        private Execute(@NotNull SConfig config, @NotNull ConfigValue<?> adapter, @NotNull Parameter.Value<Object> newValue) {
            this.adapter = (ConfigValue<T>) adapter;
            this.newValue = (Parameter.Value<T>) newValue;
            this.config = config;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            return SetConfigCommand.execute(context.cause().audience(), this.config, this.adapter, context.requireOne(this.newValue));
        }
    }

    private SetConfigCommand() {
        throw new RuntimeException("Cannot run");
    }

    public static Command.Parameterized createSetConfigCommand(SConfig config) {
        Command.Builder command = Command.builder();
        for (ConfigValue<?> configValue : config.expectedNodes()) {
            Optional<Parameter.Value.Builder<Object>> opParameter = SParameters.parameterFor(configValue.type());
            if (!opParameter.isPresent()) {
                EssentialsSMain.plugin().logger().warn("Cannot find a command parameter for " + configValue.type().getSimpleName());
                continue;
            }
            Parameter.Value<Object> parameter = opParameter.get().key("value").build();
            Command.Parameterized cmd = Command.builder().addParameter(parameter).executor(new Execute<>(config, configValue, parameter)).build();
            command.addChild(cmd, Arrays.stream(configValue.nodes()).map(Object::toString).collect(Collectors.joining(".")));
        }
        return command.permission(SPermissions.CONFIG_SET_MESSAGE.node()).build();
    }

    public static <T> CommandResult execute(@NotNull Audience audience, @NotNull SConfig config, @NotNull ConfigValue<T> configValue, @NotNull T value) {
        try {
            ConfigurationLoader<? extends ConfigurationNode> loader = config.configurationLoader();
            ConfigurationNode root = loader.load();
            configValue.set(root, value);

            Component messageValue;
            if (value instanceof Component) {
                messageValue = ((Component) value);
            } else {
                messageValue = FriendlyStrings
                        .ofType(value)
                        .map(friendly -> friendly.toFriendlyComponent(value))
                        .orElseGet(() -> Component.text(value.toString()));
            }
            audience.sendMessage(Component
                                         .text("set ")
                                         .append(Component
                                                         .text(Arrays.stream(configValue.nodes()).map(Object::toString).collect(Collectors.joining(".")))
                                                         .color(NamedTextColor.AQUA))
                                         .append(Component.text(" to "))
                                         .append(messageValue));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Component.text(e.getLocalizedMessage()));
        }
        return CommandResult.success();
    }
}
