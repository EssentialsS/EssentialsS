package org.essentialss.implementation.config;

import org.essentialss.api.config.GeneralConfig;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.api.utils.Singleton;
import org.jetbrains.annotations.NotNull;

public class SConfigManagerImpl implements SConfigManager {

    private final @NotNull Singleton<GeneralConfig> generalConfig = new Singleton<>(SGeneralConfigImpl::new);

    @Override
    public @NotNull Singleton<GeneralConfig> general() {
        return this.generalConfig;
    }
}
