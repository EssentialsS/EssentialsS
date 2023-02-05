package org.essentialss.implementation;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.essentialss.api.EssentialsSAPI;
import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.implementation.world.SWorldManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("essentials-s")
public class EssentialsSMain implements EssentialsSAPI {

    private final PluginContainer container;
    private final Logger logger;

    private static EssentialsSMain plugin;

    private final Singleton<SWorldManager> worldManager = new Singleton<>(SWorldManagerImpl::new);

    @Inject
    public EssentialsSMain(PluginContainer container, Logger logger) {
        this.plugin = this;
        this.container = container;
        this.logger = logger;
    }

    public @NotNull PluginContainer container() {
        return this.container;
    }

    public @NotNull Logger logger() {
        return this.logger;
    }


    @Override
    public @NotNull Singleton<SWorldManager> worldManager() {
        return this.worldManager;
    }

    @Override
    public @NotNull Singleton<SPlayerManager> playerManager() {
        throw new RuntimeException("Player manager not implemented");
    }

    public static EssentialsSMain plugin() {
        return plugin;
    }
}
