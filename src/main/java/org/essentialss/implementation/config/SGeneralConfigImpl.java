package org.essentialss.implementation.config;

import org.essentialss.api.config.GeneralConfig;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.implementation.EssentialsSMain;
import org.spongepowered.api.Sponge;

import javax.naming.ConfigurationException;
import java.io.File;

public class SGeneralConfigImpl implements GeneralConfig {

    private static final


    @Override
    public SingleConfigValue.Default<Integer> pageSize() {
        return null;
    }

    @Override
    public File file() {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        return new File(folder, "configs/general.conf");
    }

    @Override
    public void generateDefault() throws ConfigurationException {

    }
}
