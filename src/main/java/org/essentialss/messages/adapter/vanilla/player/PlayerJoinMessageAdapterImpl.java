package org.essentialss.messages.adapter.vanilla.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.vanilla.player.PlayerJoinMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.vanilla.AbstractVanillaMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.LinkedList;

public class PlayerJoinMessageAdapterImpl extends AbstractVanillaMessageAdapter implements PlayerJoinMessageAdapter {

    public static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue configValue = new ComponentConfigValue("vanilla", "player", "connection", "join", "Message");
        Component defaultValue = Component.text(SPlaceHolders.PLAYER_NAME.formattedPlaceholderTag() + " joined the game").color(NamedTextColor.YELLOW);

        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(configValue, defaultValue);
    }

    public PlayerJoinMessageAdapterImpl() {
        super(false, CONFIG_VALUE);
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component messageToAdapt, @NotNull SGeneralPlayerData playerData) {
        MessageManager manager = EssentialsSMain.plugin().messageManager().get();
        for (SPlaceHolder<SGeneralPlayerData> placeholder : manager.mappedPlaceholdersFor(SGeneralPlayerData.class)) {
            messageToAdapt = placeholder.apply(messageToAdapt, playerData);
        }
        for (SPlaceHolder<Player> placeHolder : manager.mappedPlaceholdersFor(Player.class)){
            messageToAdapt = placeHolder.apply(messageToAdapt, playerData.spongePlayer());
        }
        return messageToAdapt;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        Collection<SPlaceHolder<?>> placeHolders = new LinkedList<>();
        MessageManager manager = EssentialsSMain.plugin().messageManager().get();
        placeHolders.addAll(manager.placeholdersFor(SGeneralUnloadedData.class));
        placeHolders.addAll(manager.placeholdersFor(Player.class));
        return placeHolders;
    }
}
