package org.essentialss.messages.adapter.player.command.mute;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.message.adapters.player.command.mute.YouAreNowMutedMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.message.placeholder.wrapper.collection.AndCollectionWrapperPlaceholder;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("i-am-message-adapter")
public class YouAreNowMutedMessageAdapterImpl extends AbstractMessageAdapter implements YouAreNowMutedMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue configValue = new ComponentConfigValue("player", "command", "mute", "YouAreNowMuted");
        Component defaultValue = Component.text("You are now muted with %" + SPlaceHolders.MUTE_TYPE.placeholderTag() + "'s%");
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(configValue, defaultValue);
    }

    public YouAreNowMutedMessageAdapterImpl() {
        super(CONFIG_VALUE);
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component message, @NotNull MuteType... types) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();

        List<? extends AndCollectionWrapperPlaceholder<MuteType>> muteTypePlaceholders = messageManager
                .mappedPlaceholdersFor(MuteType.class)
                .stream()
                .map(AndCollectionWrapperPlaceholder::new)
                .collect(Collectors.toList());
        for (AndCollectionWrapperPlaceholder<MuteType> placeholder : muteTypePlaceholders) {
            message = placeholder.apply(message, Arrays.asList(types));
        }

        return message;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        return messageManager
                .placeholdersFor(MuteType.class)
                .stream()
                .map(placeholder -> (SPlaceHolder<?>) new AndCollectionWrapperPlaceholder<>(placeholder))
                .collect(Collectors.toList());
    }
}
