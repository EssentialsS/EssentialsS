package org.essentialss.command.invsee;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.UUID;

public final class InventorySeeCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> player;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player) {
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            SGeneralPlayerData player = context.requireOne(this.player);
            Object root = context.cause().root();
            if (!(root instanceof ServerPlayer)) {
                Component playerOnlyCommandMessage = EssentialsSMain.plugin().messageManager().get().adapters().playerOnlyCommand().get().adaptMessage();
                return CommandResult.error(playerOnlyCommandMessage);
            }
            Player thisPlayer = (Player) root;
            PlayerInventory playerInventory = player.spongePlayer().inventory();

            return InventorySeeCommand.execute(EssentialsSMain.plugin().playerManager().get().dataFor(thisPlayer), player.displayName(), playerInventory,
                                               player.uuid());
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

    public static CommandResult execute(@NotNull SGeneralPlayerData playerData,
                                        @NotNull Component title,
                                        @NotNull Inventory playerInventory,
                                        @NotNull UUID playerId) {
        Player player = playerData.spongePlayer();
        if (!(player instanceof ServerPlayer)) {
            return CommandResult.error(Component.text("Server only command"));
        }
        ServerPlayer sPlayer = (ServerPlayer) player;
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
        if (sPlayer.hasPermission(SPermissions.INVENTORY_SEE_WRITE.node())) {
            menu.registerClose((cause, container) -> playerData.removeViewingInventoryOf());
        } else {
            menu = menu.setReadOnly(true);
        }
        menu.open(sPlayer);
        playerData.setViewingInventoryOf(playerId);
        return CommandResult.success();
    }
}
