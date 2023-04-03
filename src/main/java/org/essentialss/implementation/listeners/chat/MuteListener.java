package org.essentialss.implementation.listeners.chat;

import net.kyori.adventure.audience.Audience;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.EssentialsSMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.service.permission.Subject;

public class MuteListener {

    @Listener(beforeModifications = true, order = Order.FIRST)
    public void onPlayerCommand(ExecuteCommandEvent.Pre event) {
        Subject subject = event.commandCause().subject();
        if (!(subject instanceof Player)) {
            return;
        }
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) subject);
        if (playerData.muteTypes().contains(MuteType.COMMAND)) {
            event.setCancelled(true);
        }
    }

    @Listener(beforeModifications = true, order = Order.FIRST)
    public void onPlayerMessage(PlayerChatEvent event, @First Player sender) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(sender);
        if (playerData.muteTypes().contains(MuteType.MESSAGE)) {
            event.setCancelled(true);
            return;
        }
        if (!playerData.muteTypes().contains(MuteType.PRIVATE)) {
            return;
        }
        Audience audience = event.audience().orElseGet(event::originalAudience);
        if (audience instanceof Player) {
            event.setCancelled(true);
        }
    }

}
