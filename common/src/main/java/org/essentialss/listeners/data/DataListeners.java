package org.essentialss.listeners.data;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.world.points.OfflineLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.plugin.PluginContainer;

import java.util.Optional;
import java.util.UUID;

public class DataListeners {

    @Listener(beforeModifications = true)
    public void changeWorld(ChangeEntityWorldEvent.Post event, @Getter("entity") Player player) {
        MovementType movementType = event
                .context()
                .get(EventContextKeys.MOVEMENT_TYPE)
                .orElseThrow(() -> new RuntimeException("Change world event does not contain movement type"));
        if (movementType.equals(MovementTypes.END_GATEWAY.get())) {
            return;
        }
        if (movementType.equals(MovementTypes.PORTAL.get())) {
            return;
        }

        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);

        OfflineLocation location = new OfflineLocation(player.location());
        playerData.addBackTeleportLocation(location);
    }

    @Listener(order = Order.LAST)
    public void onInvSeeClick(AffectSlotEvent event, @First ServerPlayer player) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
        Optional<UUID> opViewingPlayerId = playerData.viewingInventoryOf();
        if (!opViewingPlayerId.isPresent()) {
            return;
        }
        Optional<ServerPlayer> opViewingPlayer = Sponge.server().player(opViewingPlayerId.get());
        if (!opViewingPlayer.isPresent()) {
            player.closeInventory();
            return;
        }

        PlayerInventory inv = opViewingPlayer.get().inventory();

        event.transactions().forEach(slotTransaction -> {
            ItemStackSnapshot originalItem = slotTransaction.original();
            ItemStackSnapshot finalItem = slotTransaction.custom().orElse(slotTransaction.finalReplacement());

            Sponge.server().scheduler().executor(EssentialsSMain.plugin().container()).execute(() -> {
                Inventory playerModifySlots = inv.query(QueryTypes.ITEM_STACK_EXACT.get().of(originalItem.createStack()));
                if (!playerModifySlots.slots().isEmpty()) {
                    Slot slot = playerModifySlots.slots().iterator().next();
                    slot.set(finalItem.createStack());
                }
            });
        });
    }

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        EssentialsSMain.plugin().worldManager().get().dataFor(event.world()).isLoadedProperty().onValueUpdate(false, true);
    }

    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        EssentialsSMain.plugin().worldManager().get().dataFor(event.world()).isLoadedProperty().onValueUpdate(true, false);
    }

    @Listener(beforeModifications = true)
    public void teleportListener(MoveEntityEvent event, @Getter("entity") Player player) {
        MovementType movementType = event.context().get(EventContextKeys.MOVEMENT_TYPE).orElse(MovementTypes.PLUGIN.get());
        if (movementType.equals(MovementTypes.PLUGIN.get())) {
            Optional<PluginContainer> opPlugin = event.context().get(EventContextKeys.PLUGIN);
            if (opPlugin.isPresent() && opPlugin.get().equals(EssentialsSMain.plugin().container())) {
                //no need to dupe the teleport
                return;
            }
        }

        if (movementType.equals(MovementTypes.END_GATEWAY.get())) {
            return;
        }
        if (movementType.equals(MovementTypes.NATURAL.get())) {
            return;
        }
        if (movementType.equals(MovementTypes.ENDER_PEARL.get())) {
            return;
        }
        if (movementType.equals(MovementTypes.PORTAL.get())) {
            return;
        }

        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);

        OfflineLocation location = new OfflineLocation(player.location());
        playerData.addBackTeleportLocation(location);
    }
}
