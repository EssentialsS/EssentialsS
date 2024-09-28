package org.essentialss.command.hat;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.events.player.hat.ChangeHatEventImpl;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Optional;

public final class HatCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<ItemStackSnapshot> itemParameter;
        private final @NotNull Parameter.Value<SGeneralPlayerData> targetParameter;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> target, @NotNull Parameter.Value<ItemStackSnapshot> item) {
            this.itemParameter = item;
            this.targetParameter = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData playerData = CommandHelper.playerDataOrTarget(context, this.targetParameter);
            Player finalPlayer = playerData.spongePlayer();
            Optional<ItemStack> opItem = context.one(this.itemParameter).map(ItemStackSnapshot::createStack);
            ItemStack item;
            Integer hotbarClearItem = null;
            if (opItem.isPresent()) {
                item = opItem.get();
            } else {
                Hotbar hotbar = finalPlayer.inventory().hotbar();
                item = hotbar
                        .peekAt(hotbar.selectedSlotIndex())
                        .map(ItemStack::copy)
                        .orElseThrow(() -> new CommandException(Component.text("item must be specified")));
                hotbarClearItem = hotbar.selectedSlotIndex();
            }

            CommandResult result = HatCommand.execute(finalPlayer, item, context.contextCause());
            if (result.isSuccess() && (null != hotbarClearItem)) {
                Hotbar hotbar = finalPlayer.inventory().hotbar();
                Slot slot = hotbar.slot(hotbarClearItem).orElseThrow(() -> new RuntimeException("Hotbar slot doesn't exist"));
                slot.clear();
            }
            return result;
        }
    }

    private HatCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createHatCommand() {
        Parameter.Value<ItemStackSnapshot> itemParameter = Parameter
                .itemStackSnapshot()
                .key("hat")
                .requiredPermission(SPermissions.HAT_CREATE_ITEM.node())
                .optional()
                .build();

        Parameter.Value<SGeneralPlayerData> targetParameter = SParameters
                .onlinePlayer((PlayerData -> true))
                .key("player")
                .optional()
                .requiredPermission(SPermissions.HAT_OTHER.node())
                .build();

        return Command.builder().executor(new Execute(targetParameter, itemParameter)).addParameter(targetParameter).addParameter(itemParameter).build();

    }

    public static CommandResult execute(@NotNull Player player, @NotNull ItemStack item, @NotNull Cause cause) {
        EquipmentInventory equipment = player.inventory().equipment();
        Optional<ItemStack> currentItem = equipment.peek(EquipmentTypes.HEAD);

        Transaction<ItemStack> transaction = new Transaction<>(currentItem.orElse(ItemStack.empty()), item);
        ChangeHatEventImpl changeHatEvent = new ChangeHatEventImpl(player, transaction, cause);
        Sponge.eventManager().post(changeHatEvent);
        if (changeHatEvent.isCancelled()) {
            return CommandResult.error(Component.text("A plugin cancelled this command"));
        }

        currentItem.ifPresent(itemStack -> player.inventory().offer(itemStack));
        equipment.set(EquipmentTypes.HEAD, changeHatEvent.item().custom().orElseGet(() -> changeHatEvent.item().finalReplacement()));
        return CommandResult.success();
    }
}
