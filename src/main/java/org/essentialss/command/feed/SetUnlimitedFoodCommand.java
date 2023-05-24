package org.essentialss.command.feed;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

final class SetUnlimitedFoodCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> player;
        private final Parameter.Value<Boolean> isUnlimited;

        private Execute(Parameter.Value<SGeneralPlayerData> player, Parameter.Value<Boolean> isUnlimited) {
            this.isUnlimited = isUnlimited;
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.player);
            boolean isEnabled = context.one(this.isUnlimited).orElseGet(() -> !player.unlimitedFood());

            player.unlimitedFoodProperty().setValue(isEnabled);
            return CommandResult.success();
        }
    }

    private SetUnlimitedFoodCommand() {
        throw new RuntimeException("Should not generate");
    }

    static Command.Parameterized createSetUnlimitedFoodCommand() {
        Parameter.Value<SGeneralPlayerData> player = SParameters
                .onlinePlayer(p -> true)
                .key("player")
                .requiredPermission(SPermissions.FEED_OTHER.node())
                .optional()
                .build();
        Parameter.Value<Boolean> enabled = Parameter.bool().key("enabled").optional().build();

        return Command.builder().addParameter(enabled).addParameter(player).executor(new Execute(player, enabled)).build();
    }

}
