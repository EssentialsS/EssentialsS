package org.essentialss.implementation;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.essentialss.api.EssentialsSAPI;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.implementation.command.hat.HatCommand;
import org.essentialss.implementation.command.nick.NicknameCommand;
import org.essentialss.implementation.command.point.PointCommand;
import org.essentialss.implementation.command.point.list.ListSpawnCommand;
import org.essentialss.implementation.command.point.list.ListWarpCommand;
import org.essentialss.implementation.command.run.RunCommand;
import org.essentialss.implementation.config.SConfigManagerImpl;
import org.essentialss.implementation.player.SPlayerManagerImpl;
import org.essentialss.implementation.world.SWorldManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("essentials-s")
public class EssentialsSMain implements EssentialsSAPI {

    private final PluginContainer container;
    private final Logger logger;

    private static EssentialsSMain plugin;

    private final Singleton<SWorldManager> worldManager = new Singleton<>(SWorldManagerImpl::new);
    private final Singleton<SConfigManager> configManager = new Singleton<>(SConfigManagerImpl::new);
    private final Singleton<SPlayerManager> playerManager = new Singleton<>(SPlayerManagerImpl::new);

    public static final int MINIMUM_PAGE_SIZE = 1;

    @SuppressWarnings("AccessStaticViaInstance")
    @Inject
    public EssentialsSMain(PluginContainer container, Logger logger) {
        this.plugin = this;
        this.container = container;
        this.logger = logger;
    }

    @Listener
    public void onCommandRegister(RegisterCommandEvent<Command.Parameterized> event) {
        event.register(this.container, RunCommand.createRunCommand(), "run", "execute");
        event.register(this.container, HatCommand.createHatCommand(), "hat");
        event.register(this.container, PointCommand.createWarpCommand(), "warp");
        event.register(this.container, ListWarpCommand.createWarpListCommand(), "warps");
        event.register(this.container, PointCommand.createSpawnCommand(), "spawn");
        event.register(this.container, ListSpawnCommand.createSpawnListCommand(), "spawns");
        event.register(this.container, NicknameCommand.createNicknameCommand(), "nickname", "nick");
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
        return this.playerManager;
    }

    @Override
    public @NotNull Singleton<SConfigManager> configManager() {
        return this.configManager;
    }

    public static EssentialsSMain plugin() {
        return plugin;
    }
}
