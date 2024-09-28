package org.essentialss.listeners.chat;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.message.adapters.vanilla.player.PlayerJoinMessageAdapter;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class VanillaMessageListener {

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        PlayerJoinMessageAdapter playerJoinAdapter = EssentialsSMain.plugin().messageManager().get().adapters().playerJoin().get();
        if (!playerJoinAdapter.isEnabled()) {
            event.setMessageCancelled(true);
            return;
        }
        if (playerJoinAdapter.isUsingVanilla()) {
            return;
        }

        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(event.player());
        Component message = playerJoinAdapter.adaptMessage(playerData);
        event.setMessage(message);
    }

}
