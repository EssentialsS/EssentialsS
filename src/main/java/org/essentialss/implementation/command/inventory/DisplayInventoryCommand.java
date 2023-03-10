package org.essentialss.implementation.command.inventory;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.command.CommandUtils;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.function.Supplier;

public final class DisplayInventoryCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> player;
        private final @NotNull Supplier<ContainerType> supplier;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player, @NotNull Supplier<ContainerType> supplier) {
            this.player = player;
            this.supplier = supplier;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandUtils.getTarget(context, this.player,
                                                               () -> new CommandException(Component.text("player needs to be specified")));
            if (!(player.spongePlayer() instanceof ServerPlayer)) {
                return CommandResult.error(Component.text("server only command"));
            }
            return DisplayInventoryCommand.execute((ServerPlayer) player.spongePlayer(), this.supplier);
        }
    }

    private DisplayInventoryCommand() {
        throw new RuntimeException("Cannot run");
    }

    public static Command.Parameterized createAnvilInventoryCommand() {
        Parameter.Value<SGeneralPlayerData> playerData = SParameters
                .onlinePlayer(t -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.ANVIL_OTHER.node())
                .build();

        return Command
                .builder()
                .addParameter(playerData)
                .executor(new Execute(playerData, ContainerTypes.ANVIL))
                .permission(SPermissions.ANVIL_SELF.node())
                .build();
    }

    public static Command.Parameterized createBlastFurnaceInventoryCommand() {
        Parameter.Value<SGeneralPlayerData> playerData = SParameters
                .onlinePlayer(t -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.BLAST_FURNACE_OTHER.node())
                .build();

        return Command
                .builder()
                .addParameter(playerData)
                .executor(new Execute(playerData, ContainerTypes.BLAST_FURNACE))
                .permission(SPermissions.BLAST_FURNACE_SELF.node())
                .build();
    }

    public static Command.Parameterized createBrewInventoryCommand() {
        Parameter.Value<SGeneralPlayerData> playerData = SParameters
                .onlinePlayer(t -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.BREW_OTHER.node())
                .build();

        return Command
                .builder()
                .addParameter(playerData)
                .executor(new Execute(playerData, ContainerTypes.BREWING_STAND))
                .permission(SPermissions.BREW_SELF.node())
                .build();
    }

    public static Command.Parameterized createCraftingInventoryCommand() {
        Parameter.Value<SGeneralPlayerData> playerData = SParameters
                .onlinePlayer(t -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.CRAFTING_OTHER.node())
                .build();

        return Command
                .builder()
                .addParameter(playerData)
                .executor(new Execute(playerData, ContainerTypes.CRAFTING))
                .permission(SPermissions.CRAFTING_SELF.node())
                .build();
    }

    public static Command.Parameterized createFurnaceInventoryCommand() {
        Parameter.Value<SGeneralPlayerData> playerData = SParameters
                .onlinePlayer(t -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.FURNACE_OTHER.node())
                .build();

        return Command
                .builder()
                .addParameter(playerData)
                .executor(new Execute(playerData, ContainerTypes.FURNACE))
                .permission(SPermissions.FURNACE_SELF.node())
                .build();
    }

    public static CommandResult execute(@NotNull ServerPlayer player, Supplier<ContainerType> suppliers) {
        ViewableInventory inv = ViewableInventory
                .builder()
                .type(suppliers)
                .completeStructure()
                .plugin(EssentialsSMain.plugin().container())
                .carrier(player)
                .build();
        InventoryMenu menu = inv.asMenu();

        menu.open(player);
        return CommandResult.success();
    }

}
