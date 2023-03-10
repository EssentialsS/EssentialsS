package org.essentialss.implementation.command.essentialss.config.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.message.placeholder.MessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.utils.Constants;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.stream.Collectors;

public final class SetMessageCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<MessageAdapter> adapter;
        private final @NotNull Parameter.Value<Component> newMessage;

        private Execute(@NotNull Parameter.Value<MessageAdapter> adapter, @NotNull Parameter.Value<Component> newMessage) {
            this.adapter = adapter;
            this.newMessage = newMessage;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return SetMessageCommand.execute(context.cause().audience(), context.requireOne(this.adapter), context.requireOne(this.newMessage));
        }
    }

    private SetMessageCommand() {
        throw new RuntimeException("Cannot run");
    }

    public static Command.Parameterized createSetMessageCommand() {
        Parameter.Value<MessageAdapter> adapter = SParameters.messageAdapter().key("type").build();
        Parameter.Value<Component> newMessage = Parameter.formattingCodeTextOfRemainingElements().completer((context, currentInput) -> {
            MessageAdapter messageAdapter = context.requireOne(adapter);
            Collection<SPlaceHolder<?>> placeholders = messageAdapter.supportedPlaceholders();

            boolean withSpace = currentInput.endsWith(" ");

            return placeholders.parallelStream().map(SPlaceHolder::formattedPlaceholderTag).filter(placeholder -> {
                int percentagesLength = currentInput.split("%").length - 1;
                if (Constants.ZERO == (percentagesLength % 2)) {
                    return true;
                }
                int lastIndex = currentInput.lastIndexOf("%");
                String lastKnown = currentInput.substring(lastIndex);
                if (lastKnown.length() > placeholder.length()) {
                    return false;
                }
                return placeholder.toLowerCase().startsWith(currentInput.toLowerCase());
            }).map(placeholder -> CommandCompletion.of(currentInput + (withSpace ? "" : " ") + placeholder)).collect(Collectors.toList());
        }).key("newMessage").build();

        return Command
                .builder()
                .addParameter(adapter)
                .addParameter(newMessage)
                .executor(new Execute(adapter, newMessage))
                .permission(SPermissions.CONFIG_SET_MESSAGE.node())
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull MessageAdapter configValue, @NotNull Component message) {
        try {
            configValue.setUnadaptedMessage(message);
            audience.sendMessage(message);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Component.text(e.getLocalizedMessage()));
        }
        return CommandResult.success();
    }
}
