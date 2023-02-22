package org.essentialss.implementation.listeners.connection;

import net.kyori.adventure.text.Component;
import org.essentialss.api.ban.BanMultiplayerScreenOptions;
import org.essentialss.api.ban.SBanManager;
import org.essentialss.api.ban.data.BanData;
import org.essentialss.api.ban.data.IPBanData;
import org.essentialss.api.config.BanConfig;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Optional;

public class BanConnectionListeners {

    private Collection<BanData<?>> isBanned(@NotNull ServerSideConnection connection) {
        return EssentialsSMain.plugin().banManager().get().banData(connection);
    }

    @Listener
    public void onPlayerHandshake(ServerSideConnectionEvent.Handshake event) {
        SBanManager banManager = EssentialsSMain.plugin().banManager().get();
        try {
            banManager.reloadFromConfig();
        } catch (ConfigurateException ignored) {
        }

        Collection<BanData<?>> banData = this.isBanned(event.connection());
        if (banData.isEmpty()) {
            return;
        }
        BanConfig config = banManager.banConfig().get();
        Component banMessage = config.banMessage().parseDefault(config);
        if (config.useBanMessageForTempBan().parseDefault(config)) {
            event.connection().close(banMessage);
            return;
        }

        if (banData.parallelStream().noneMatch(data -> data.bannedUntil().isPresent())) {
            //full ban
            event.connection().close(banMessage);
            return;
        }
        //temp ban
        try {
            Component tempBanMessage = config.tempBanMessage().parse(config);
            if (null != tempBanMessage) {
                banMessage = tempBanMessage;
            }
        } catch (SerializationException ignored) {
        }

        event.connection().close(banMessage);

    }

    @Listener(order = Order.EARLY)
    public void onPlayerPing(ClientPingServerEvent event) {
        SBanManager banManager = EssentialsSMain.plugin().banManager().get();
        try {
            banManager.reloadFromConfig();
        } catch (ConfigurateException ignored) {
        }
        Optional<IPBanData> isIpBanned = banManager
                .banData(IPBanData.class)
                .stream()
                .filter(ipBan -> ipBan.hostName().equalsIgnoreCase(event.client().address().getHostName()))
                .findAny();
        if (!isIpBanned.isPresent()) {
            return;
        }

        BanConfig config = banManager.banConfig().get();
        BanMultiplayerScreenOptions options = config.showBanOnMultiplayerScreen().parseDefault(config);
        if (BanMultiplayerScreenOptions.NO_CONNECTION == options) {
            event.setCancelled(true);
            return;
        }

        ClientPingServerEvent.Response response = event.response();
        if (config.showFullOnMultiplayerScreen().parseDefault(config)) {
            response.players().ifPresent(players -> players.setMax(players.online()));
        }

        if (BanMultiplayerScreenOptions.HIDDEN_PLAYERS == options) {
            response.setHidePlayers(true);
            return;
        }
        if (BanMultiplayerScreenOptions.BAN_MESSAGE == options) {
            boolean temp = isIpBanned.get().bannedUntil().isPresent();
            Component message = null;
            if (temp && config.useBanMessageForTempBan().parseDefault(config)) {
                try {
                    message = config.tempBanMessage().parse(config);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
            if (null == message) {
                message = config.banMessage().parseDefault(config);
            }
            response.setDescription(message);
        }
    }

}
