package org.essentialss.config.configs;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.GeneralConfig;
import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.config.value.ListDefaultConfigValueImpl;
import org.essentialss.config.value.primitive.BooleanConfigValue;
import org.essentialss.config.value.primitive.IntegerConfigValue;
import org.essentialss.config.value.simple.RegistryConfigValue;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class SGeneralConfigImpl implements GeneralConfig {

    private static final IntegerConfigValue PAGE_SIZE;
    private static final BooleanConfigValue CHECK_FOR_UPDATE_ON_LAUNCH;
    private static final ListDefaultConfigValueImpl<DamageType> DEMI_GOD_IMMUNE_TO;

    static {
        PAGE_SIZE = new IntegerConfigValue(10, "misc", "ListPageSize");
        CHECK_FOR_UPDATE_ON_LAUNCH = new BooleanConfigValue(false, "update", "CheckOnStartup");
        DEMI_GOD_IMMUNE_TO = new ListDefaultConfigValueImpl<>(RegistryConfigValue.damageType(), () -> DamageTypes
                .registry()
                .stream()
                .filter(type -> !type.equals(DamageTypes.ATTACK.get()))
                .filter(type -> !type.equals(DamageTypes.VOID.get()))
                .collect(Collectors.toList()), "demigod", "ImmuneFrom");
    }

    @Override
    public SingleConfigValue.Default<Boolean> checkForUpdatesOnStartup() {
        return CHECK_FOR_UPDATE_ON_LAUNCH;
    }

    @Override
    public CollectionConfigValue.Default<DamageType> demiGodImmuneTo() {
        return DEMI_GOD_IMMUNE_TO;
    }

    @Override
    public SingleConfigValue.Default<Integer> pageSize() {
        return PAGE_SIZE;
    }

    @Override
    @SuppressWarnings("ReturnOfNull")
    public @NotNull Collection<ConfigValue<?>> expectedNodes() {
        return Arrays
                .stream(SGeneralConfigImpl.class.getDeclaredFields())
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
        return new File(folder, "config/General.conf");
    }

    @Override
    public void update() throws ConfigurateException, SerializationException {
        ConfigurationLoader<? extends ConfigurationNode> loader = this.configurationLoader();
        ConfigurationNode node = loader.load();
        PAGE_SIZE.setDefaultIfNotPresent(node);
        CHECK_FOR_UPDATE_ON_LAUNCH.setDefaultIfNotPresent(node);
        loader.save(node);
    }
}
