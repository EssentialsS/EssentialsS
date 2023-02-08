package org.essentialss.implementation.config;

import org.essentialss.api.config.GeneralConfig;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.api.utils.Singleton;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.config.ConfigManager;

public class SConfigManagerImpl implements SConfigManager {
    @Override
    public @NotNull Singleton<GeneralConfig> general() {
        return null;
    }
}
