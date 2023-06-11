package org.essentialss.command.kit;

import net.kyori.adventure.audience.Audience;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.permissions.permission.SPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public final class RemoveKitCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Kit> kitParameter;

        private Execute(Parameter.Value<Kit> kitParameter) {
            this.kitParameter = kitParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Kit kit = context.requireOne(this.kitParameter);
            return RemoveKitCommand.execute(context.cause().audience(), kit);
        }
    }

    private RemoveKitCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createRemoveKitCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters
                .onlinePlayer(p -> true)
                .optional()
                .requiredPermission(SPermissions.ADD_KIT_OTHER.node())
                .key("player")
                .build();
        Parameter.Value<Kit> kitParameter = SParameters.kitParameter((context, kit) -> true).key("kit").build();

        return Command.builder().permission(SPermissions.REMOVE_KIT.node()).executor(new Execute(kitParameter)).addParameter(kitParameter).build();
    }

    public static CommandResult execute(Audience audience, Kit kit) {
        EssentialsSMain.plugin().kitManager().get().unregister(kit);
        return CommandResult.success();
    }

}
