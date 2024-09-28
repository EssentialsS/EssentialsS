package org.essentialss.messages.adapter.player.command;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.command.PingMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.Singleton;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.LinkedList;

@SuppressWarnings("i-am-message-adapter")
public class PingMessageAdapterImpl extends AbstractMessageAdapter implements PingMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue configValue = new ComponentConfigValue("player", "command", "Ping");
        Component defaultValue = Component.text(
                SPlaceHolders.PLAYER_NICKNAME.formattedPlaceholderTag() + " has a ping of " + SPlaceHolders.PLAYER_PING.formattedPlaceholderTag());

        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(configValue, defaultValue);
    }

    public PingMessageAdapterImpl() {
        super(CONFIG_VALUE);
    }

    public PingMessageAdapterImpl(@SuppressWarnings("TypeMayBeWeakened") @NotNull MessageConfig config) {
        super(new Singleton<>(() -> {
            try {
                return CONFIG_VALUE.parse(config);
            } catch (SerializationException e) {
                //noinspection ReturnOfNull
                return null;
            }
        }));
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
}
