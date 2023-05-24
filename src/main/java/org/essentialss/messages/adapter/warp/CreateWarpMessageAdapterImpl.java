package org.essentialss.messages.adapter.warp;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.warp.CreateWarpMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("i-am-message-adapter")
public class CreateWarpMessageAdapterImpl extends AbstractMessageAdapter implements CreateWarpMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue messageConfigValue = new ComponentConfigValue("warp", "Create");
        Component defaultMessage = Component.text("created warp of " + SPlaceHolders.POINT_NAME.copyWithTagType("warp").formattedPlaceholderTag());
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(messageConfigValue, defaultMessage);
    }

    public CreateWarpMessageAdapterImpl() {
        super(CONFIG_VALUE);
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
}
