package org.essentialss.command.kit;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.kit.KitBuilder;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.misc.StringHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

public class AddKitCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> playerParameter;
        private final Parameter.Value<String> displayNameParameter;

        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter, Parameter.Value<String> displayNameParameter) {
            this.displayNameParameter = displayNameParameter;
            this.playerParameter = playerParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            String displayName = context.requireOne(this.displayNameParameter);
            String idName = StringHelper.toIdFormat(displayName);
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            return AddKitCommand.execute(context.cause().audience(), player.spongePlayer().inventory(), idName, displayName);
        }
    }

    public static Command.Parameterized createAddKitCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters
                .onlinePlayer(p -> true)
                .optional()
                .requiredPermission(SPermissions.ADD_KIT_OTHER.node())
                .key("player")
                .build();
        Parameter.Value<String> displayNameParameter = Parameter.string().key("name").build();

        return Command
                .builder()
                .executor(new Execute(playerParameter, displayNameParameter))
                .addParameter(displayNameParameter)
                .addParameter(playerParameter)
                .build();
    }

    public static CommandResult execute(Audience audience, PlayerInventory inv, String idName, @Nullable String displayName) {
        try {
            Kit kit = new KitBuilder()
                    .addKitSlots(inv)
                    .setDisplayName(displayName)
                    .setIdName(idName)
                    .setPlugin(EssentialsSMain.plugin().container())
                    .register();
            audience.sendMessage(Component.text("Registered " + kit.displayName()));
        } catch (IllegalStateException e) {
            return CommandResult.error(Component.text(e.getMessage()));
        }
        return CommandResult.success();
    }

}
