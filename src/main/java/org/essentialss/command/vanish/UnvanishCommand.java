package org.essentialss.command.vanish;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.VanishState;

public class UnvanishCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> playerParameter;


        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter) {
            this.playerParameter = playerParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            return UnvanishCommand.execute(player);
        }
    }

    public static Command.Parameterized createUnvanishCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters.onlinePlayer(p -> true).key("player").optional().build();

        return Command
                .builder()
                .addParameter(playerParameter)
                .executor(new Execute(playerParameter))
                .build();
    }

    public static CommandResult execute(SGeneralPlayerData player) {
        VanishState vanish = VanishState.unvanished();

        player.spongePlayer().offer(Keys.VANISH_STATE, vanish);
        return CommandResult.success();
    }
}
