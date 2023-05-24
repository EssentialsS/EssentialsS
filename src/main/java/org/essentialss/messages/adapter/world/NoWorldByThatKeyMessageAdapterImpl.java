package org.essentialss.messages.adapter.world;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.adapters.world.NoWorldByThatKeyMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.world.SWorldData;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("i-am-message-adapter")
public class NoWorldByThatKeyMessageAdapterImpl extends AbstractMessageAdapter implements NoWorldByThatKeyMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue messageConfigValue = new ComponentConfigValue("world", "error", "NoWorldByThatKey");
        Component defaultMessage = Component.text(
                "No world by the key of " + SPlaceHolders.RESOURCE_KEY.copyWithTagType("world").formattedPlaceholderTag() + ".");
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(messageConfigValue, defaultMessage);
    }

    public NoWorldByThatKeyMessageAdapterImpl() {
        super(CONFIG_VALUE);
    }

    @NotNull
    @Override
    public Component adaptMessage(@NotNull Component messageToAdapt, @NotNull ResourceKey worldKey) {
        MessageManager messageManager = EssentialsSMain.plugin().messageManager().get();
        for (SPlaceHolder<ResourceKey> placeholder : messageManager.mappedPlaceholdersFor(ResourceKey.class)) {
            messageToAdapt = placeholder.copyWithTagType("world").apply(messageToAdapt, worldKey);
        }
        SWorldData worldData = EssentialsSMain.plugin().worldManager().get().dataFor(worldKey);
        for (SPlaceHolder<SWorldData> placeholder : messageManager.mappedPlaceholdersFor(SWorldData.class)) {
            messageToAdapt = placeholder.apply(messageToAdapt, worldData);
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
        Collection<SPlaceHolder<?>> placeHolders = new ArrayList<>();
        placeHolders.addAll(messageManager.placeholdersFor(ResourceKey.class));
        placeHolders.addAll(messageManager.placeholdersFor(SWorldData.class));
        return placeHolders;
    }
}
