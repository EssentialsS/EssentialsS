package org.essentialss.implementation.command.essentialss.config.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.message.placeholder.MessageAdapter;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public class ViewMessageCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<MessageAdapter> adapter;

        private Execute(@NotNull Parameter.Value<MessageAdapter> adapter) {
            this.adapter = adapter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return ViewMessageCommand.execute(context.cause().audience(), context.requireOne(this.adapter));
        }
    }

    public static Command.Parameterized createViewMessageCommand() {
        Parameter.Value<MessageAdapter> adapter = SParameters.messageAdapter().key("type").build();

        return Command
                .builder()
                .addParameter(adapter)
                .executor(new Execute(adapter))
                .permission(SPermissions.CONFIG_VIEW_MESSAGE.node())
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull MessageAdapter configValue) {
        @NotNull Component message = configValue.unadaptedMessage();
        audience.sendMessage(message);
        return CommandResult.success();
    }
}
