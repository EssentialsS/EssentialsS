package org.essentialss.implementation.messages.adapter.warp;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.warp.CreateWarpMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.implementation.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CreateWarpMessageAdapterImpl implements CreateWarpMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("warp", "Create"),
            Component.text("created warp of " + SPlaceHolders.POINT_NAME.copyWithTagType("warp").formattedPlaceholderTag()));
    private final @Nullable Component message;

    public CreateWarpMessageAdapterImpl() {
        Component com;
        try {
            com = CONFIG_VALUE.parse(EssentialsSMain.plugin().messageManager().get().config().get());
        } catch (SerializationException e) {
            com = null;
        }
        this.message = com;
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component messageToAdapt, @NotNull SWarp warp) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        for (SPlaceHolder<SWarp> placeholder : messageManager.mappedPlaceholdersFor(SWarp.class)) {
            messageToAdapt = placeholder.copyWithTagType("warp").apply(messageToAdapt, warp);
        }
        for (SPlaceHolder<SWarp> placeholder : messageManager.mappedPlaceholdersFor(SWarp.class)) {
            messageToAdapt = placeholder.apply(messageToAdapt, warp);
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
        List<SPlaceHolder<?>> placeHolders = new ArrayList<>(messageManager.placeholdersFor(SWarp.class));
        placeHolders.addAll(messageManager.placeholdersFor(SPoint.class));
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
