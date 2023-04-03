package org.essentialss.implementation.messages.adapter.player.command.mute;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.message.adapters.player.command.mute.UnmutedMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.message.placeholder.wrapper.collection.AndCollectionWrapperPlaceholder;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.implementation.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.stream.Collectors;

public class UnmutedMessageAdapterImpl implements UnmutedMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("player", "command", "mute", "Unmuted"),
            Component.text("You unmuted " + SPlaceHolders.PLAYER_NAME.formattedPlaceholderTag()));
    private @Nullable Component message;

    public UnmutedMessageAdapterImpl() {
        try {
            this.message = CONFIG_VALUE.parse(EssentialsSMain.plugin().messageManager().get().config().get());
        } catch (SerializationException e) {
            this.message = null;
        }
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component message, @NotNull SGeneralUnloadedData playerData, @NotNull MuteType... types) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();

        for (SPlaceHolder<SGeneralUnloadedData> placeholder : messageManager.mappedPlaceholdersFor(SGeneralUnloadedData.class)) {
            message = placeholder.apply(message, playerData);
        }

        Optional<GameProfile> opProfile = playerData.profile();
        if (opProfile.isPresent()) {
            for (SPlaceHolder<GameProfile> placeholder : messageManager.mappedPlaceholdersFor(GameProfile.class)) {
                message = placeholder.apply(message, opProfile.get());
            }
        }

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

        Collection<SPlaceHolder<?>> placeholders = new LinkedList<>();
        placeholders.addAll(messageManager.placeholdersFor(SGeneralUnloadedData.class));
        placeholders.addAll(messageManager.placeholdersFor(GameProfile.class));
        placeholders.addAll(messageManager
                                    .placeholdersFor(MuteType.class)
                                    .stream()
                                    .map(placeholder -> (SPlaceHolder<?>) new AndCollectionWrapperPlaceholder<>(placeholder))
                                    .collect(Collectors.toList()));
        return placeholders;
    }

    @Override
    public @NotNull Component unadaptedMessage() {
        if (null == this.message) {
            return this.defaultUnadaptedMessage();
        }
        return this.message;
    }
}
