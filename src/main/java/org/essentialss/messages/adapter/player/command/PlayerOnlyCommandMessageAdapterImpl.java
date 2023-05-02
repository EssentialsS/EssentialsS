package org.essentialss.messages.adapter.player.command;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.adapters.player.command.PlayerOnlyCommandMessageAdapter;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;

public class PlayerOnlyCommandMessageAdapterImpl implements PlayerOnlyCommandMessageAdapter {

    private static final SingleDefaultConfigValueWrapper<Component> CONFIG_VALUE = new SingleDefaultConfigValueWrapper<>(
            new ComponentConfigValue("player", "command", "PlayerOnlyCommand"), Component.text("Sorry. This command can only be ran as a player"));
    private final @Nullable Component message;

    public PlayerOnlyCommandMessageAdapterImpl(@NotNull MessageConfig config) {
        Component com;
        try {
            com = CONFIG_VALUE.parse(config);
        } catch (SerializationException e) {
            com = null;
        }
        this.message = com;
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

    @Override
    public @NotNull Component unadaptedMessage() {
        if (null == this.message) {
            return this.defaultUnadaptedMessage();
        }
        return this.message;
    }
}
