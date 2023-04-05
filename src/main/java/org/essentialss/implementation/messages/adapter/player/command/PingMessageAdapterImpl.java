package org.essentialss.implementation.messages.adapter.player.command;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.command.PingMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.implementation.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.LinkedList;

public class PingMessageAdapterImpl implements PingMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("player", "command", "Ping"),
            Component.text(SPlaceHolders.PLAYER_NICKNAME.formattedPlaceholderTag() + " has a ping of " + SPlaceHolders.PLAYER_PING.formattedPlaceholderTag()));
    private final @Nullable Component message;

    public PingMessageAdapterImpl() {
        this(EssentialsSMain.plugin().messageManager().get().config().get());
    }

    public PingMessageAdapterImpl(@NotNull MessageConfig config) {
        Component com;
        try {
            com = CONFIG_VALUE.parse(config);
        } catch (SerializationException e) {
            com = null;
        }
        this.message = com;
    }

    @Override
    public Component adaptMessage(@NotNull Component messageToAdapt, @NotNull SGeneralPlayerData player) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();

        messageToAdapt = messageManager.adaptMessageFor(messageToAdapt, player);
        messageToAdapt = messageManager.adaptMessageFor(messageToAdapt, player.spongePlayer());
        return messageToAdapt;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        Collection<SPlaceHolder<?>> collection = new LinkedList<>();
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        collection.addAll(messageManager.placeholdersFor(ServerPlayer.class));
        collection.addAll(messageManager.placeholdersFor(SGeneralPlayerData.class));

        return collection;
    }

    @Override
    public @NotNull Component unadaptedMessage() {
        if (null == this.message) {
            return this.defaultUnadaptedMessage();
        }
        return this.message;
    }
}
