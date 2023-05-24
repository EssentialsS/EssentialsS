package org.essentialss.command.feed;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.Constants;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;

public final class SetFoodLevelCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> level;
        private final Parameter.Value<SGeneralPlayerData> player;

        private Execute(Parameter.Value<SGeneralPlayerData> player, Parameter.Value<Integer> level) {
            this.player = player;
            this.level = level;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.player);
            Integer level = context.one(this.level).orElse(null);
            return SetFoodLevelCommand.execute(player, level);
        }
    }

    private SetFoodLevelCommand() {
        throw new RuntimeException("Should not create");
    }

    static Command.Builder createFoodCommand(Command.Builder builder) {
        Parameter.Value<SGeneralPlayerData> player = SParameters
                .onlinePlayer(p -> true)
                .key("player")
                .requiredPermission(SPermissions.FEED_OTHER.node())
                .optional()
                .build();
        Parameter.Value<Integer> foodLevel = Parameter.rangedInteger(0, Constants.MAX_FOOD_LEVEL).key("food").optional().build();
        return builder.executor(new Execute(player, foodLevel)).addParameter(foodLevel).addParameter(player);
    }

    static Command.Parameterized createFoodCommand() {
        return createFoodCommand(Command.builder()).build();
    }

    public static CommandResult execute(SGeneralPlayerData player, Integer level) {
        if (null == level) {
            level = player.spongePlayer().getInt(Keys.MAX_FOOD_LEVEL).orElse(Constants.MAX_FOOD_LEVEL);
        }
        player.spongePlayer().offer(Keys.FOOD_LEVEL, level);
        return CommandResult.success();
    }


}
