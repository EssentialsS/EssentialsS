package org.essentialss.implementation.config.configs;

import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.message.placeholder.MessageAdapter;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SMessageConfigImpl implements MessageConfig {
    @Override
    public @NotNull File file() {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        return new File(folder, "messages.conf");
    }

    @Override
    public void update() throws SerializationException, ConfigurateException {
        ConfigurationLoader<? extends ConfigurationNode> loader = this.configurationLoader();
        ConfigurationNode root = loader.load();

        List<MessageAdapter> adapters = EssentialsSMain.plugin().messageManager().get().adapters().all().collect(Collectors.toList());
        for (MessageAdapter adapter : adapters) {
            adapter.configValue().setDefaultIfNotPresent(root);
        }
    }
}
