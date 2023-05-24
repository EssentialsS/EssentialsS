package org.essentialss.messages.adapter.player.listener.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.listener.chat.ChatMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.chat.format.ChatFormat;
import org.essentialss.api.message.adapters.player.listener.chat.format.ChatFormats;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractEnabledMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("i-am-message-adapter")
public class SChatMessageAdapterImpl extends AbstractEnabledMessageAdapter implements ChatMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> MAIN_VALUE;
    private final Collection<ChatFormat> formatters = new LinkedList<>(Arrays.asList(ChatFormats.values()));

    static {
        ComponentConfigValue component = new ComponentConfigValue("player", "chat", "chatFormat", "Message");
        Component defaultValue = Component.text(
                "[" + SPlaceHolders.PLAYER_NICKNAME.formattedPlaceholderTag() + "] " + SPlaceHolders.MESSAGE_TEXT.formattedPlaceholderTag());

        MAIN_VALUE = new SingleDefaultConfigValueWrapper<>(component, defaultValue);
    }

    public SChatMessageAdapterImpl() {
        super(false, MAIN_VALUE);
    }


    @Override
    public Component adaptMessage(@NotNull Component unformatted, @NotNull ServerPlayer player, @NotNull Audience receiver, @NotNull Component message) {

        List<? extends SPlaceHolder<Component>> placeholders = this
                .supportedPlaceholders()
                .stream()
                .map(placeholder -> (SPlaceHolder<Component>) placeholder)
                .collect(Collectors.toList());

        for (SPlaceHolder<Component> placeholder : placeholders) {
            unformatted = placeholder.apply(unformatted, message);
        }

        return unformatted;
    }

    @Override
    public Component formatMessage(@NotNull ServerPlayer player, @NotNull Audience receiver, @NotNull Component message, @NotNull Component originalMessage) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
        for (ChatFormat format : this.formatters) {
            message = format.adapt(player, playerData, receiver, message, originalMessage);
        }
        return message;
    }

    @Override
    public Collection<ChatFormat> formatters() {
        return this.formatters;
    }

    @Override
    public void register(@NotNull ChatFormat chatFormat) {
        this.formatters.add(chatFormat);
    }

    @NotNull
    @Override
    public SingleConfigValue.Default<Component> configValue() {
        return MAIN_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        @NotNull MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        return messageManager
                .placeholdersFor(SPlaceHolders.MESSAGE)
                .stream()
                .filter(placeholder -> Component.class.isAssignableFrom(placeholder.type()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
