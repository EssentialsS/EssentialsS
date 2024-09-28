package org.essentialss.messages.adapter;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.adapters.MessageAdapter;
import org.essentialss.api.utils.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class AbstractMessageAdapter implements MessageAdapter {

    private final @NotNull Singleton<Component> message;

    protected AbstractMessageAdapter(@NotNull Singleton<Component> componentSupplier) {
        this.message = componentSupplier;
    }

    protected AbstractMessageAdapter(SingleConfigValue.Default<Component> configValue) {
        this.message = new Singleton<>(() -> {
            try {
                return configValue.parse(EssentialsSMain.plugin().messageManager().get().config().get());
            } catch (SerializationException e) {
                //noinspection ReturnOfNull
                return null;
            }
        });
    }

    @Override
    public @NotNull Component unadaptedMessage() {
        @Nullable Component message = this.message.get();
        if (null == message) {
            return this.defaultUnadaptedMessage();
        }
        return message;
    }
}
