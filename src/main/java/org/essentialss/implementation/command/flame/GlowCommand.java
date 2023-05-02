package org.essentialss.implementation.command.flame;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.misc.CommandHelper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class GlowCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> playerParameter;

        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter) {
            this.playerParameter = playerParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            return GlowCommand.execute(player);
        }
    }

    public static Command.Parameterized createGlowCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters.onlinePlayer(p -> true).key("player").optional().build();
        return Command.builder().executor(new Execute(playerParameter)).addParameter(playerParameter).build();
    }

    public static CommandResult execute(SGeneralPlayerData player) {
        boolean isGlowing = player.spongePlayer().get(Keys.IS_GLOWING).orElse(false);
        Player spongePlayer = player.spongePlayer();

        spongePlayer.offer(Keys.IS_GLOWING, !isGlowing);
        return CommandResult.success();
    }

}
