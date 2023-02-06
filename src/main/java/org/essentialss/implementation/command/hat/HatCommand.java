package org.essentialss.implementation.command.hat;

import net.kyori.adventure.text.Component;
import org.essentialss.implementation.events.hat.ChangeHatEventImpl;
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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Optional;

public class HatCommand {

    private static class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<ItemStackSnapshot> itemParameter;
        private final @NotNull Parameter.Value<ServerPlayer> targetParameter;

        public Execute(@NotNull Parameter.Value<ServerPlayer> target,
                       @NotNull Parameter.Value<ItemStackSnapshot> item) {
            this.itemParameter = item;
            this.targetParameter = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerPlayer> opPlayer = context.one(this.targetParameter);
            Player player;
            if (opPlayer.isPresent()) {
                player = opPlayer.get();
            } else if (context.subject() instanceof Player) {
                player = (Player) context.subject();
            } else {
                throw new CommandException(Component.text("Player must be specified"));
            }
            final Player finalPlayer = player;
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
                Slot slot = hotbar
                        .slot(hotbarClearItem)
                        .orElseThrow(() -> new RuntimeException("Hotbar slot doesn't exist"));
                slot.clear();
            }
            return result;
        }
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
        equipment.set(EquipmentTypes.HEAD,
                      changeHatEvent.item().custom().orElseGet(() -> changeHatEvent.item().finalReplacement()));
        return CommandResult.success();
    }


    public static Command.Parameterized createHatCommand() {
        Parameter.Key<ItemStackSnapshot> itemKey = Parameter.key("hat", ItemStackSnapshot.class);
        Parameter.Value<ItemStackSnapshot> itemParameter = Parameter
                .itemStackSnapshot()
                .key(itemKey)
                .optional()
                .build();

        Parameter.Key<ServerPlayer> playerKey = Parameter.key("player", ServerPlayer.class);
        Parameter.Value<ServerPlayer> targetParameter = Parameter.playerOrTarget().key(playerKey).optional().build();

        return Command
                .builder()
                .executor(new Execute(targetParameter, itemParameter))
                .addParameter(targetParameter)
                .addParameter(itemParameter)
                .build();

    }
}
