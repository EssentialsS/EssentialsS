package org.essentialss.implementation.listeners.connection;

import net.kyori.adventure.text.Component;
import org.essentialss.api.message.MessageData;
import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.player.SPlayerManagerImpl;
import org.essentialss.implementation.player.data.AbstractProfileData;
import org.essentialss.implementation.player.data.SPlayerDataImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.configurate.ConfigurateException;

public class ConnectionListeners {

    @Listener
    public void onPlayerJoinServer(ServerSideConnectionEvent.Join event) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(event.player());
        if (!playerData.mailMessages().isEmpty()) {
            Component message = Component.text(
                    "You have " + playerData.mailMessages().size() + " messages in your inbox");
            playerData.sendMessageTo(new MessageData(message));
        }
    }

    @Listener
    public void onPlayerLeaveServer(ServerSideConnectionEvent.Disconnect event) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(event.player());
        try {
            playerData.saveToConfig();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
        if (!(playerData instanceof SPlayerDataImpl)) {
            return;
        }
        SPlayerManager playerManager = EssentialsSMain.plugin().playerManager().get();
        if (!(playerManager instanceof SPlayerManagerImpl)) {
            return;
        }
        ((SPlayerManagerImpl) playerManager).unload((AbstractProfileData) playerData);
    }

}
