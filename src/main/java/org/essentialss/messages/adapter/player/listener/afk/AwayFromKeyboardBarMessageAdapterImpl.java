package org.essentialss.messages.adapter.player.listener.afk;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardBarMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AwayFromKeyboardBarMessageAdapterImpl implements AwayFromKeyboardBarMessageAdapter {
    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("player", "interaction", "AwayFromKeyboardBar"),
            Component.text(SPlaceHolders.DURATION.formattedPlaceholderTag() + " until you are kicked"));
    private final @Nullable Component message;

    public AwayFromKeyboardBarMessageAdapterImpl() {
        Component com;
        try {
            com = CONFIG_VALUE.parse(EssentialsSMain.plugin().messageManager().get().config().get());
        } catch (SerializationException e) {
            com = null;
        }
        this.message = com;
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component messageToAdapt, @NotNull Duration left) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        messageToAdapt = messageManager.adaptMessageFor(messageToAdapt, left);
        return messageToAdapt;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        List<SPlaceHolder<?>> placeHolders = new ArrayList<>(messageManager.placeholdersFor(Duration.class));
        return new SingleUnmodifiableCollection<>(placeHolders);
    }

    @Override
    public @NotNull Component unadaptedMessage() {
        if (null == this.message) {
            return this.defaultUnadaptedMessage();
        }
        return this.message;
    }
}
