package org.essentialss.messages.adapter.player.command;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.adapters.player.command.PlayerOnlyCommandMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.utils.Singleton;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.messages.adapter.AbstractMessageAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;

public class PlayerOnlyCommandMessageAdapterImpl extends AbstractMessageAdapter implements PlayerOnlyCommandMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE;

    static {
        ComponentConfigValue configValue = new ComponentConfigValue("player", "command", "PlayerOnlyCommand");
        Component defaultValue = Component.text("Sorry. This command can only be ran as a player");
        CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(configValue, defaultValue);
        ;
    }

    public PlayerOnlyCommandMessageAdapterImpl(@NotNull MessageConfig config) {
        super(new Singleton<>(() -> {
            try {
                return CONFIG_VALUE.parse(config);
            } catch (SerializationException e) {
                return null;
            }
        }));
    }

    @Override
    public @NotNull Component adaptMessage(@NotNull Component message) {
        return message;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Component> configValue() {
        return CONFIG_VALUE;
    }

    @Override
    public @NotNull Collection<SPlaceHolder<?>> supportedPlaceholders() {
        return Collections.emptyList();
    }
}
