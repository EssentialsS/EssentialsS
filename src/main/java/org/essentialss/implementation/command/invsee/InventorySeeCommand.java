package org.essentialss.implementation.command.invsee;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.service.permission.Subject;

public final class InventorySeeCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> player;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player) {
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            SGeneralPlayerData player = context.requireOne(this.player);
            Subject subject = context.subject();
            if (!(subject instanceof ServerPlayer)) {
                Component playerOnlyCommandMessage = EssentialsSMain.plugin().messageManager().get().adapters().playerOnlyCommand().get().adaptMessage();
                return CommandResult.error(playerOnlyCommandMessage);
            }
            ServerPlayer thisPlayer = (ServerPlayer) subject;
            PlayerInventory playerInventory = player.spongePlayer().inventory();

            return InventorySeeCommand.execute(thisPlayer, player.displayName(), playerInventory);
        }
    }

    private InventorySeeCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createInventorySeeCommand() {
        Parameter.Value<SGeneralPlayerData> player = SParameters.onlinePlayer(p -> true).key("player").build();
        return Command
                .builder()
                .addParameter(player)
                .executor(new Execute(player))
                .executionRequirements(c -> Sponge.isServerAvailable() && (c.subject() instanceof ServerPlayer))
                .build();
    }

    public static CommandResult execute(@NotNull ServerPlayer player, @NotNull Component title, @NotNull Inventory playerInventory) {
        ViewableInventory showingInventory = ViewableInventory
                .builder()
                .type(ContainerTypes.GENERIC_9X6)
                .completeStructure()
                .plugin(EssentialsSMain.plugin().container())
                .build();
        showingInventory.offer(
                playerInventory.slots().stream().map(slot -> slot.peek().copy()).filter(item -> !item.equalTo(ItemStack.empty())).toArray(ItemStack[]::new));

        InventoryMenu menu = showingInventory.asMenu();
        menu.setTitle(title);
        menu = menu.setReadOnly(true);
        menu.open(player);
        return CommandResult.success();
    }
}
