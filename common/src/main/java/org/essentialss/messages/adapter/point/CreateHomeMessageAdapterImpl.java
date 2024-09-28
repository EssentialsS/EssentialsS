package org.essentialss.messages.adapter.point;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.point.CreateHomeMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("i-am-message-adapter")
public class CreateHomeMessageAdapterImpl extends AbstractMessageAdapter implements CreateHomeMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue messageConfigValue = new ComponentConfigValue("home", "Create");
        Component defaultMessage = Component.text("created home of " + SPlaceHolders.POINT_NAME.copyWithTagType("home").formattedPlaceholderTag());
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(messageConfigValue, defaultMessage);
    }

    public CreateHomeMessageAdapterImpl() {
        super(CONFIG_VALUE);
    }

    @NotNull
    @Override
    public Component adaptMessage(@NotNull Component messageToAdapt, @NotNull SHome home) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        for (SPlaceHolder<SHome> placeholder : messageManager.mappedPlaceholdersFor(SHome.class)) {
            messageToAdapt = placeholder.copyWithTagType("home").apply(messageToAdapt, home);
        }
        for (SPlaceHolder<SHome> placeholder : messageManager.mappedPlaceholdersFor(SHome.class)) {
            messageToAdapt = placeholder.apply(messageToAdapt, home);
        }
        return messageToAdapt;
    }


    @NotNull
    @Override
    public SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @NotNull
    @Override
    public Collection<SPlaceHolder<?>> supportedPlaceholders() {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        List<SPlaceHolder<?>> placeHolders = new ArrayList<>(messageManager.placeholdersFor(SHome.class));
        placeHolders.addAll(messageManager.placeholdersFor(SPoint.class));
        return new SingleUnmodifiableCollection<>(placeHolders);
    }
}
