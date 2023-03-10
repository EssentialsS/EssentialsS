package org.essentialss.implementation.config.configs;

import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.modifier.SPlayerModifiers;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.ListDefaultConfigValueImpl;
import org.essentialss.implementation.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.implementation.config.value.primitive.BooleanConfigValue;
import org.essentialss.implementation.config.value.simple.DurationConfigValue;
import org.essentialss.implementation.config.value.simple.PlayerModifierConfigValue;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.time.Duration;
import java.util.Collections;

public class AwayFromKeyboardConfigImpl implements AwayFromKeyboardConfig {

    private static final SingleConfigValue.Default<Boolean> CAN_JOIN_IN_PLACE_OF_AFK_PLAYER = new BooleanConfigValue(true, "join", "KickIfServerIsFull");
    private static final SingleConfigValue.Default<Boolean> SHOW_AFK_PLAYERS_ON_MULTIPLAYER_SCREEN = new BooleanConfigValue("join",
                                                                                                                            "ShowAfkPlayersOnMultiplayerScreen");
    private static final SingleConfigValue.Default<Boolean> LOCKED_POSITION = new BooleanConfigValue("general", "LockPosition");
    private static final SingleConfigValue<Duration> DURATION_UNTIL_MODIFIERS = new DurationConfigValue("general", "duration", "modifier", "Delay");
    private static final SingleConfigValue.Default<Boolean> APPLY_MODIFIERS = new BooleanConfigValue("general", "duration", "modifier", "Apply");
    private static final SingleConfigValue.Default<Duration> DURATION_UNTIL_STATUS = new SingleDefaultConfigValueWrapper<>(
            new DurationConfigValue("general", "duration", "Status"), Duration.ofSeconds(3));

    private static final CollectionConfigValue<SPlayerModifier<?>> MODIFIERS = new ListDefaultConfigValueImpl<>(new PlayerModifierConfigValue(), "general",
                                                                                                                "duration", "modifier", "Modifiers");
    private static final SingleConfigValue<Duration> DURATION_UNTIL_KICK = new DurationConfigValue("general", "duration", "kick", "Delay");
    private static final SingleConfigValue.Default<Boolean> WILL_KICK_AFTER_DURATION = new BooleanConfigValue("general", "duration", "kick", "WillKick");


    @Override
    public SingleConfigValue.Default<Boolean> canJoinInPlaceOfAwayFromKeyboard() {
        return CAN_JOIN_IN_PLACE_OF_AFK_PLAYER;
    }

    @Override
    public SingleConfigValue<Duration> durationUntilKick() {
        return DURATION_UNTIL_KICK;
    }

    @Override
    public SingleConfigValue<Duration> durationUntilModifiers() {
        return DURATION_UNTIL_MODIFIERS;
    }

    @Override
    public SingleConfigValue<Duration> durationUntilStatus() {
        return DURATION_UNTIL_STATUS;
    }

    @Override
    public SingleConfigValue.Default<Boolean> lockPosition() {
        return LOCKED_POSITION;
    }

    @Override
    public CollectionConfigValue<SPlayerModifier<?>> modifiers() {
        return MODIFIERS;
    }

    @Override
    public SingleConfigValue.Default<Boolean> showAwayFromKeyboardPlayersOnMultiplayerScreen() {
        return SHOW_AFK_PLAYERS_ON_MULTIPLAYER_SCREEN;
    }


    @Override
    public @NotNull File file() {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        return new File(folder, "config/afk.conf");
    }

    @Override
    public void update() throws SerializationException, ConfigurateException {
        ConfigurationLoader<? extends ConfigurationNode> loader = this.configurationLoader();
        ConfigurationNode root = loader.load();
        CAN_JOIN_IN_PLACE_OF_AFK_PLAYER.setDefaultIfNotPresent(root);
        SHOW_AFK_PLAYERS_ON_MULTIPLAYER_SCREEN.setDefaultIfNotPresent(root);
        LOCKED_POSITION.setDefaultIfNotPresent(root);
        if (null == DURATION_UNTIL_MODIFIERS.parse(root)) {
            DURATION_UNTIL_MODIFIERS.set(root, Duration.ofSeconds(3));
        }
        if (null == MODIFIERS.parse(root)) {
            MODIFIERS.set(root, Collections.singletonList(SPlayerModifiers.VISIBILITY));
        }
        APPLY_MODIFIERS.setDefaultIfNotPresent(root);
        DURATION_UNTIL_STATUS.setDefaultIfNotPresent(root);
        if (null == DURATION_UNTIL_KICK.parse(root)) {
            DURATION_UNTIL_KICK.set(root, Duration.ofMinutes(1));
        }
        WILL_KICK_AFTER_DURATION.setDefaultIfNotPresent(root);
        loader.save(root);
    }
}
