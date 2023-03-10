package org.essentialss.implementation.listeners.connection;

import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.message.adapters.player.listener.afk.BackToKeyboardMessageAdapter;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.entity.RotateEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class AwayFromKeyboardListeners {


    private void onAction(@NotNull Player sPlayer) {
        SGeneralPlayerData player = EssentialsSMain.plugin().playerManager().get().dataFor(sPlayer);
        boolean isAfk = player.isShowingAwayFromKeyboard();
        player.playerAction();
        if (!isAfk) {
            return;
        }
        if (!Sponge.isServerAvailable()) {
            return;
        }
        BackToKeyboardMessageAdapter messageAdapter = EssentialsSMain.plugin().messageManager().get().adapters().backToKeyboard().get();
        Sponge.server().broadcastAudience().sendMessage(messageAdapter.adaptMessage(player));
    }

    @Listener
    public void onPlayerHandshake(ServerSideConnectionEvent.Handshake event) {
        int maxPlayers = Sponge.server().maxPlayers();
        Collection<SGeneralPlayerData> players = EssentialsSMain.plugin().playerManager().get().allPlayerData();
        if (players.size() != maxPlayers) {
            return;
        }

        AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
        if (config.showAwayFromKeyboardPlayersOnMultiplayerScreen().parseDefault(config)) {
            return;
        }

        Optional<SGeneralPlayerData> opToKick = players.stream().max(Comparator.comparing(SGeneralPlayerData::lastPlayerAction));
        if (!opToKick.isPresent()) {
            //players are 0?
            return;
        }
        ServerPlayer toKick = (ServerPlayer) opToKick.get().spongePlayer();
        toKick.kick();
    }

    @Listener
    public void onPlayerPing(ClientPingServerEvent event) {
        Optional<ClientPingServerEvent.Response.Players> opPlayers = event.response().players();
        if (!opPlayers.isPresent()) {
            return;
        }
        ClientPingServerEvent.Response.Players players = opPlayers.get();
        if (players.max() != players.online()) {
            return;
        }

        AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
        if (config.showAwayFromKeyboardPlayersOnMultiplayerScreen().parseDefault(config)) {
            return;
        }
        int playersAFK = (int) EssentialsSMain
                .plugin()
                .playerManager()
                .get()
                .allPlayerData()
                .stream()
                .filter(SGeneralPlayerData::isShowingAwayFromKeyboard)
                .count();
        players.setOnline(players.online() - playersAFK);
    }

    @Listener
    public void playerMoveListener(MoveEntityEvent event) {
        if (!(event.entity() instanceof Player)) {
            return;
        }
        SGeneralPlayerData player = EssentialsSMain.plugin().playerManager().get().dataFor((Player) event.entity());
        AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
        if (player.isShowingAwayFromKeyboard() && config.lockPosition().parseDefault(config)) {
            event.setCancelled(true);
            return;
        }
        this.onAction(player.spongePlayer());
    }

    @Listener
    public void playerRideListener(RideEntityEvent event) {
        //is entity the rider or the entity being ridden?
        //api 10 -> this will be changed to have both
        //api 8-9 -> the source
        if (!(event.entity() instanceof Player)) {
            return;
        }
        this.onAction((Player) event.entity());
    }

    @Listener
    @Exclude(CollideBlockEvent.class)
    public void playerRootCause(Event event, @Root Player player) {
        this.onAction(player);
    }

    @Listener
    public void playerRotateListener(RotateEntityEvent event) {
        if (!(event.entity() instanceof Player)) {
            return;
        }
        this.onAction((Player) event.entity());
    }


}
