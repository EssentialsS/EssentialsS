package org.essentialss.command.spy;

import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public final class CommandSpyCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralUnloadedData> player;
        private final Parameter.Value<Boolean> enabled;

        private Execute(Parameter.Value<SGeneralUnloadedData> player, Parameter.Value<Boolean> enabled) {
            this.player = player;
            this.enabled = enabled;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralUnloadedData player = CommandHelper.playerDataOrTarget(context, this.player);
            boolean enabled = context.one(this.enabled).orElseGet(() -> !player.isCommandSpying());
            return CommandSpyCommand.execute(player, enabled);
        }
    }

    private CommandSpyCommand() {
        throw new RuntimeException("Do not create");
    }

    public static Command.Parameterized createCommandSpyCommand() {
        Parameter.Value<SGeneralUnloadedData> playerParameter = SParameters
                .offlinePlayersNickname(false, (p) -> true)
                .requiredPermission(SPermissions.COMMAND_SPY_OTHER.node())
                .key("player")
                .optional()
                .build();
        Parameter.Value<Boolean> booleanParameter = Parameter.bool().key("enabled").optional().build();
        return Command
                .builder()
                .addParameter(playerParameter)
                .addParameter(booleanParameter)
                .executor(new Execute(playerParameter, booleanParameter))
                .permission(SPermissions.COMMAND_SPY_SELF.node())
                .build();
    }

    public static CommandResult execute(@NotNull SGeneralUnloadedData player, boolean enabled) {
        player.setCommandSpying(enabled);
        return CommandResult.success();
    }

}
