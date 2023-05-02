package org.essentialss.config.configs;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.modifier.SPlayerModifiers;
import org.essentialss.config.value.ListDefaultConfigValueImpl;
import org.essentialss.config.value.modifiers.SingleDefaultConfigValueWrapper;
import org.essentialss.config.value.modifiers.rely.AbstractRelyOnConfigValue;
import org.essentialss.config.value.modifiers.rely.NullableRelyOnConfigValue;
import org.essentialss.config.value.simple.DurationConfigValue;
import org.essentialss.config.value.simple.PlayerModifierConfigValue;
import org.essentialss.config.value.primitive.BooleanConfigValue;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class AwayFromKeyboardConfigImpl implements AwayFromKeyboardConfig {

    private static final SingleConfigValue.Default<Boolean> CAN_JOIN_IN_PLACE_OF_AFK_PLAYER = new BooleanConfigValue(true, "join", "KickIfServerIsFull");
    private static final SingleConfigValue.Default<Boolean> SHOW_AFK_PLAYERS_ON_MULTIPLAYER_SCREEN = new BooleanConfigValue("join",
                                                                                                                            "ShowAfkPlayersOnMultiplayerScreen");
    private static final SingleConfigValue.Default<Boolean> LOCKED_POSITION = new BooleanConfigValue("general", "LockPosition");
    private static final SingleConfigValue<Duration> DURATION_UNTIL_MODIFIERS = new DurationConfigValue("general", "duration", "modifier", "Delay");
    private static final SingleConfigValue.Default<Boolean> APPLY_MODIFIERS = new BooleanConfigValue("general", "duration", "modifier", "Apply");
    private static final SingleConfigValue.Default<Duration> DURATION_UNTIL_STATUS = new SingleDefaultConfigValueWrapper<>(
            new DurationConfigValue("general", "duration", "Status"), Duration.ofSeconds(30));

    private static final CollectionConfigValue<SPlayerModifier<?>> MODIFIERS = new ListDefaultConfigValueImpl<>(new PlayerModifierConfigValue(), "general",
                                                                                                                "duration", "modifier", "Modifiers");
    private static final SingleConfigValue.Default<Boolean> WILL_KICK_AFTER_DURATION = new BooleanConfigValue("general", "duration", "kick", "WillKick");
    @SuppressWarnings("ReturnOfNull")
    private static final AbstractRelyOnConfigValue<Duration, Boolean> DURATION_UNTIL_KICK = NullableRelyOnConfigValue.ifFalse(
            new DurationConfigValue("general", "duration", "kick", "Delay"), WILL_KICK_AFTER_DURATION);


    @Override
    public SingleConfigValue.Default<Boolean> canJoinInPlaceOfAwayFromKeyboard() {
        return CAN_JOIN_IN_PLACE_OF_AFK_PLAYER;
    }

    @Override
    public ConfigValue<Duration> durationUntilKick() {
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
    @SuppressWarnings("ReturnOfNull")
    public @NotNull Collection<ConfigValue<?>> expectedNodes() {
        return Arrays
                .stream(AwayFromKeyboardConfigImpl.class.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> ConfigValue.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (ConfigValue<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull File file() {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File originalFile = new File(folder, "config/afk.conf");
        if (originalFile.exists()) {
            return originalFile;
        }

        return new File(folder, "config/AFK.conf");
    }

    @Override
    public void update() throws ConfigurateException, SerializationException {
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
