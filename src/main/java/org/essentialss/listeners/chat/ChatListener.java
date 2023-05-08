package org.essentialss.listeners.chat;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.message.adapters.player.listener.chat.ChatMessageAdapter;
import org.spongepowered.api.entity.living.player.PlayerChatFormatter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Optional;

public class ChatListener {

    @Listener(order = Order.LAST)
    public void onChatMessage(PlayerChatEvent event) {
        PlayerChatFormatter formatter = event.chatFormatter().orElse(event.originalChatFormatter());
        ChatMessageAdapter chatAdapter = EssentialsSMain.plugin().messageManager().get().adapters().chat().get();

        event.setChatFormatter((player, target, message, originalMessage) -> {
            message = chatAdapter.formatMessage(player, target, message, originalMessage);
            if (!chatAdapter.isEnabled()) {
                try {
                    return formatter.format(player, target, message, originalMessage);
                } catch (Throwable e) {
                    throw new RuntimeException("The fault is below", e);
                }
            }
            Component adapted = chatAdapter.adaptMessage(player, target, message);
            return Optional.of(adapted);
        });

    }

}
