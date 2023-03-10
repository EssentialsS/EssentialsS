package org.essentialss.implementation;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.essentialss.api.EssentialsSAPI;
import org.essentialss.api.ban.SBanManager;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.api.config.configs.BanConfig;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.implementation.ban.SBanManagerImpl;
import org.essentialss.implementation.command.ban.BanCommands;
import org.essentialss.implementation.command.essentialss.EssentialsSCommand;
import org.essentialss.implementation.command.hat.HatCommand;
import org.essentialss.implementation.command.inventory.DisplayInventoryCommand;
import org.essentialss.implementation.command.invsee.InventorySeeCommand;
import org.essentialss.implementation.command.nick.NicknameCommand;
import org.essentialss.implementation.command.nick.WhoIsCommand;
import org.essentialss.implementation.command.point.PointCommand;
import org.essentialss.implementation.command.point.list.ListSpawnCommand;
import org.essentialss.implementation.command.point.list.ListWarpCommand;
import org.essentialss.implementation.command.run.RunCommand;
import org.essentialss.implementation.command.teleport.request.*;
import org.essentialss.implementation.command.unban.UnbanCommands;
import org.essentialss.implementation.config.SConfigManagerImpl;
import org.essentialss.implementation.listeners.connection.AwayFromKeyboardListeners;
import org.essentialss.implementation.listeners.connection.BanConnectionListeners;
import org.essentialss.implementation.listeners.connection.ConnectionListeners;
import org.essentialss.implementation.messages.SMessageManagerImpl;
import org.essentialss.implementation.player.SPlayerManagerImpl;
import org.essentialss.implementation.schedules.AwayFromKeyboardCheckScheduler;
import org.essentialss.implementation.world.SWorldManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("essentials-s")
public class EssentialsSMain implements EssentialsSAPI {

    private static EssentialsSMain plugin;
    private final PluginContainer container;
    private final Logger logger;
    private final Singleton<SWorldManager> worldManager = new Singleton<>(SWorldManagerImpl::new);
    private final Singleton<SConfigManager> configManager = new Singleton<>(SConfigManagerImpl::new);
    private final Singleton<SPlayerManager> playerManager = new Singleton<>(SPlayerManagerImpl::new);
    private final Singleton<SBanManager> banManager = new Singleton<>(SBanManagerImpl::new);
    private final Singleton<MessageManager> messageManager = new Singleton<>(SMessageManagerImpl::new);

    @SuppressWarnings("AccessStaticViaInstance")
    @Inject
    public EssentialsSMain(PluginContainer container, Logger logger) {
        this.plugin = this;
        this.container = container;
        this.logger = logger;
    }

    @Override
    public @NotNull Singleton<SBanManager> banManager() {
        return this.banManager;
    }

    @Override
    public @NotNull Singleton<SConfigManager> configManager() {
        return this.configManager;
    }

    @Override
    public @NotNull PluginContainer container() {
        return this.container;
    }

    @Override
    public @NotNull Singleton<MessageManager> messageManager() {
        return this.messageManager;
    }

    @Override
    public @NotNull Singleton<SPlayerManager> playerManager() {
        return this.playerManager;
    }

    @Override
    public @NotNull Singleton<SWorldManager> worldManager() {
        return this.worldManager;
    }

    public @NotNull Logger logger() {
        return this.logger;
    }

    @Listener
    public void onCommandRegister(RegisterCommandEvent<Command.Parameterized> event) {
        //misc
        event.register(this.container, RunCommand.createRunCommand(), "run", "execute");
        event.register(this.container, EssentialsSCommand.createEssentialsCommand(), "essentialss");

        //inventory
        event.register(this.container, HatCommand.createHatCommand(), "hat");
        event.register(this.container, InventorySeeCommand.createInventorySeeCommand(), "inventorysee", "invsee");
        event.register(this.container, DisplayInventoryCommand.createCraftingInventoryCommand(), "crafting", "workbench");
        event.register(this.container, DisplayInventoryCommand.createBrewInventoryCommand(), "brewing", "brew");
        event.register(this.container, DisplayInventoryCommand.createAnvilInventoryCommand(), "anvil");
        event.register(this.container, DisplayInventoryCommand.createFurnaceInventoryCommand(), "furnace");
        event.register(this.container, DisplayInventoryCommand.createBlastFurnaceInventoryCommand(), "blast");

        //warp
        event.register(this.container, PointCommand.createWarpCommand(), "warp");
        event.register(this.container, ListWarpCommand.createWarpListCommand(), "warps");
        //spawn
        event.register(this.container, PointCommand.createSpawnCommand(), "spawn");
        event.register(this.container, ListSpawnCommand.createSpawnListCommand(), "spawns");
        //nickname
        event.register(this.container, NicknameCommand.createNicknameCommand(), "nickname", "nick");
        event.register(this.container, WhoIsCommand.createWhoIsCommand(), "whois", "realname");
        //teleport
        event.register(this.container, TeleportRequestsCommand.createTeleportRequestsCommand(), "teleportrequests", "tprequests", "tpr");
        event.register(this.container, TeleportRequestToPlayerCommand.createTeleportRequestToPlayerCommand(), "teleportto", "tpto", "tpt");
        event.register(this.container, TeleportRequestHerePlayerCommand.createTeleportRequestHerePlayerCommand(), "teleporthere", "tphere", "tph");
        event.register(this.container, TeleportAcceptRequestCommand.createTeleportAcceptCommand(), "tpaccept", "tpa");
        event.register(this.container, TeleportDenyRequestCommand.createTeleportDenyCommand(), "tpdeny", "tpd");
        //ban
        BanConfig banConfig = this.banManager().get().banConfig().get();
        if (banConfig.useEssentialsSBanCommands().parseDefault(banConfig)) {
            event.register(this.container, BanCommands.createBanCommand(), "ban");
            event.register(this.container, UnbanCommands.createUnbanCommands(), "unban");
        }

    }

    @Listener
    public void onPluginBoot(ConstructPluginEvent event) {
        this.registerEvents();
        this.registerAsyncedSchedulers();
    }

    private void registerAsyncedSchedulers() {
        Scheduler scheduler = Sponge.asyncScheduler();
        scheduler.submit(AwayFromKeyboardCheckScheduler.createTask(), "AwayFromKeyboard");
    }

    private void registerEvents() {
        EventManager eventManager = Sponge.eventManager();
        eventManager.registerListeners(this.container, new ConnectionListeners());
        eventManager.registerListeners(this.container, new BanConnectionListeners());
        eventManager.registerListeners(this.container, new AwayFromKeyboardListeners());
    }

    public static EssentialsSMain plugin() {
        return plugin;
    }
}
