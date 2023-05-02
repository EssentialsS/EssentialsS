package org.essentialss.command.kit;

import net.kyori.adventure.text.Component;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

public class KitCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Kit> kitParameter;
        private final Parameter.Value<SGeneralPlayerData> playerParameter;
        private final boolean equip;

        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter, Parameter.Value<Kit> kitParameter, boolean equip) {
            this.kitParameter = kitParameter;
            this.playerParameter = playerParameter;
            this.equip = equip;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            Kit kit = context.requireOne(this.kitParameter);
            return KitCommand.execute(player, kit, this.equip);
        }
    }

    private static Command.Builder createGenericCommand(boolean equip) {
        Parameter.Value<SGeneralPlayerData> player = SParameters.onlinePlayer(p -> true).key("player").optional().build();
        Parameter.Value<Kit> kit = SParameters.kitParameter().key("kit").build();
        return Command.builder().executor(new Execute(player, kit, equip)).addParameter(kit).addParameter(player);
    }

    public static Command.Parameterized createKitCommand() {
        return createGenericCommand(true).addChild(createGenericCommand(true).build(), "equip").addChild(createGenericCommand(false).build(), "open").build();
    }


    public static CommandResult execute(SGeneralPlayerData player, Kit kit, boolean equip) {
        if (equip) {
            kit.apply(player.spongePlayer().inventory());
            return CommandResult.success();
        }
        if (!(player.spongePlayer() instanceof ServerPlayer)) {
            return CommandResult.error(Component.text("Server ability only"));
        }
        InventoryMenu menu = kit.createInventory().asMenu();
        menu.setTitle(Component.text(kit.displayName()));
        menu.open((ServerPlayer) player.spongePlayer());
        return CommandResult.success();
    }

}
