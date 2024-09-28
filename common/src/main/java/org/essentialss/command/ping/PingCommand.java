package org.essentialss.command.ping;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public final class PingCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> player;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player) {
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.player);
            return PingCommand.execute(context.cause().audience(), player);
        }
    }

    private PingCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createPingCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters.onlinePlayer((player) -> true).key("player").optional().build();

        return Command
                .builder()
                .addParameter(playerParameter)
                .executor(new Execute(playerParameter))
                .executionRequirements(requirement -> Sponge.isServerAvailable())
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull SGeneralPlayerData playerData) {
        Component message = EssentialsSMain.plugin().messageManager().get().adapters().ping().get().adaptMessage(playerData);
        audience.sendMessage(message);
        return CommandResult.success();
    }
}
