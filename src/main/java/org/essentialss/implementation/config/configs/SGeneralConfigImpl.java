package org.essentialss.implementation.config.configs;

import org.essentialss.api.config.configs.GeneralConfig;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.primitive.IntegerConfigValue;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
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

    private static final IntegerConfigValue PAGE_SIZE = new IntegerConfigValue(10, "misc", "ListPageSize");

    @Override
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
        return new File(folder, "configs/general.conf");
    }

    @Override
    public void update() throws SerializationException {
        ConfigurationLoader<? extends ConfigurationNode> loader = this.configurationLoader();
        ConfigurationNode node = loader.createNode();
        if (node.node(PAGE_SIZE.nodes()).isNull()) {
            PAGE_SIZE.set(node, PAGE_SIZE.defaultValue());
        }
    }

    @Override
    public SingleConfigValue.Default<Integer> pageSize() {
        return PAGE_SIZE;
    }
}
