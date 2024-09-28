package org.essentialss.config;

import org.essentialss.api.config.SConfig;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.config.configs.GeneralConfig;
import org.essentialss.api.utils.Singleton;
import org.essentialss.config.configs.AwayFromKeyboardConfigImpl;
import org.essentialss.config.configs.SGeneralConfigImpl;
import org.jetbrains.annotations.NotNull;

public class SConfigManagerImpl implements SConfigManager {

    private final @NotNull Singleton<GeneralConfig> generalConfig = SConfig.singletonLoad(SGeneralConfigImpl::new);
    private final @NotNull Singleton<AwayFromKeyboardConfig> afkConfig = SConfig.singletonLoad(AwayFromKeyboardConfigImpl::new);


    @Override
    public @NotNull Singleton<AwayFromKeyboardConfig> awayFromKeyboard() {
        return this.afkConfig;
    }

    @Override
    public @NotNull Singleton<GeneralConfig> general() {
        return this.generalConfig;
    }
}
