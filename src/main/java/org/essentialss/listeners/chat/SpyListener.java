package org.essentialss.listeners.chat;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.message.adapters.player.listener.spy.CommandSpyMessageAdapter;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;

import java.util.List;
import java.util.stream.Collectors;

public class SpyListener {

    @Listener
    public void commandSpy(ExecuteCommandEvent.Post event) {
        List<SGeneralPlayerData> players = EssentialsSMain
                .plugin()
                .playerManager()
                .get()
                .allPlayerData()
                .stream()
                .filter(SGeneralUnloadedData::isCommandSpying)
                .collect(Collectors.toList());

        if (players.isEmpty()) {
            return;
        }

        CommandSpyMessageAdapter messageAdapter = EssentialsSMain.plugin().messageManager().get().adapters().commandSpy().get();
        Component message = messageAdapter.adaptMessage(event.commandCause().subject(), event.command(), event.arguments());
        players.forEach(p -> p.spongePlayer().sendMessage(message));
    }

}
