package org.essentialss.implementation.messages.adapter.player.listener.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.listener.chat.ChatMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.chat.format.ChatFormat;
import org.essentialss.api.message.adapters.player.listener.chat.format.ChatFormats;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.implementation.config.value.modifiers.rely.AbstractRelyOnConfigValue;
import org.essentialss.implementation.config.value.modifiers.rely.NullableRelyOnConfigValue;
import org.essentialss.implementation.config.value.primitive.BooleanConfigValue;
import org.essentialss.implementation.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SChatMessageAdapterImpl implements ChatMessageAdapter {

    private static final BooleanConfigValue ENABLED_VALUE = new BooleanConfigValue("player", "chat", "Enabled");
    private static final SingleDefaultConfigValueWrapper<Component> MAIN_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("player", "chat", "ChatFormat"),
            Component.text(SPlaceHolders.DURATION.formattedPlaceholderTag() + " until you are kicked"));
    private static final AbstractRelyOnConfigValue<Component, Boolean> CONFIG_VALUE = NullableRelyOnConfigValue.ifTrue(MAIN_VALUE, ENABLED_VALUE);
    private final Collection<ChatFormat> formatters = new LinkedList<>(Arrays.asList(ChatFormats.values()));

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

    @Override
    public boolean shouldUseComponentOverride() {
        return ENABLED_VALUE.parseDefault(EssentialsSMain.plugin().configManager().get().message().get());
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

    @Override
    public @NotNull Component unadaptedMessage() {
        return MAIN_VALUE.defaultValue();
    }
}
