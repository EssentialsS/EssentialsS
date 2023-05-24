package org.essentialss.messages.adapter.player.command;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.command.WhoIsMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

@SuppressWarnings("i-am-message-adapter")
public class WhoIsCommandManagerAdapterImpl extends AbstractMessageAdapter implements WhoIsMessageAdapter {

    private static final SingleConfigValue.Default<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue configValue = new ComponentConfigValue("player", "command", "WhoIsCommand");
        Component defaultValue = Component.text(
                SPlaceHolders.PLAYER_NICKNAME.formattedPlaceholderTag() + " is " + SPlaceHolders.PLAYER_NAME.formattedPlaceholderTag());
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(configValue, defaultValue);
    }

    public WhoIsCommandManagerAdapterImpl() {
        super(CONFIG_VALUE);
    }

    @Override
    public Component adaptMessage(@NotNull Component messageToAdapt, @NotNull SGeneralUnloadedData player) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        messageToAdapt = messageManager.adaptMessageFor(messageToAdapt, player);
        if (player instanceof SGeneralPlayerData) {
            Player spongePlayer = ((SGeneralPlayerData) player).spongePlayer();
            messageToAdapt = messageManager.adaptMessageFor(messageToAdapt, spongePlayer);
        }
        return messageToAdapt;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        return messageManager.placeholdersFor(SGeneralUnloadedData.class);
    }
}
