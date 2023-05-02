package org.essentialss.command.flame;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class FlameCommand {

    private static final class Execute implements CommandExecutor {

        private @Nullable Boolean forceState;
        private Parameter.Value<SGeneralPlayerData> playerParameter;

        public Execute(Parameter.Value<SGeneralPlayerData> playerParameter, @Nullable Boolean forceState) {
            this.playerParameter = playerParameter;
            this.forceState = forceState;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            return FlameCommand.execute(player, this.forceState);
        }
    }

    private static Command.Parameterized createFlameCommand(@Nullable Boolean forceState) {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters.onlinePlayer(p -> true).key("player").optional().build();
        return Command.builder().executor(new Execute(playerParameter, forceState)).addParameter(playerParameter).build();
    }

    public static Command.Parameterized createFlameCommand() {
        return createFlameCommand(null);
    }

    public static Command.Parameterized createFlameOffCommand() {
        return createFlameCommand(false);
    }

    public static Command.Parameterized createFlameOnCommand() {
        return createFlameCommand(true);
    }

    public static CommandResult execute(SGeneralPlayerData player, @Nullable Boolean on) {
        on = (null == on) ? !player.spongePlayer().get(Keys.FIRE_TICKS).isPresent() : on;
        Player spongePlayer = player.spongePlayer();

        //KEY DOES NOT WORK ... IS THIS NOT A VALID THING ANYMORE ON THE CLIENT?
        spongePlayer.offer(Keys.IS_AFLAME, on);
        return CommandResult.success();
    }

}
