package org.essentialss.permissions.permission;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.RegistryTypes;

public enum SPermissions implements SPermission {

    MUTE("essentialss.mute"),
    KIT_OTHER("essentialss.kit.other"),
    KIT_SELF("essentialss.kit.self"),
    ABSTRACT_KIT_TYPE("essentialss.kit.type."),
    ADD_KIT_SELF("essentialss.kit.self"),
    ADD_KIT_OTHER("essentialss.kit.other"),
    REMOVE_KIT("essentialss.kit.remove"),
    KIT_LIST("essentialss.kit.list"),
    BACK_SELF("essentialss.back.self"),
    BACK_OTHER("essentialss.back.other"),
    BACK_SPACES("essentialss.back.spaces"),
    GAMEMODE_SELF("essentialss.gamemode.self"),
    GAMEMODE_SURVIVAL("essentialss.gamemode.survival"),
    GAMEMODE_CREATIVE("essentialss.gamemode.creative"),
    GAMEMODE_OTHER("essentialss.gamemode.other"),
    RANDOM_TELEPORT_SELF("essentialss.rtp.self"),
    RANDOM_TELEPORT_OTHER("essentialss.rtp.other"),
    RANDOM_TELEPORT_WORLD("essentialss.rtp.world"),
    COMMAND_SPY_OTHER("essentialss.spy.command.other"),
    COMMAND_SPY_SELF("essentialss.spy.command.self"),
    CONFIG_SET_MESSAGE("essentialss.config.message.set"),
    CONFIG_VIEW_MESSAGE("essentialss.config.message.view"),
    WARP_CREATE("essentialss.warp.create"),
    WARP_TELEPORT_SELF("essentialss.warp.teleport.self"),
    WARP_TELEPORT_OTHER("essentialsss.warp.teleport.other"),
    WARP_REMOVE("essentialss.warp.remove"),
    WARPS("essentialss.warps"),
    VIEW_TELEPORT_REQUESTS_SELF("essentailss.teleport.requests.self", true),
    VIEW_TELEPORT_REQUESTS_OTHER("essentialss.teleport.requests.other"),
    TELEPORT_REQUEST_TO_PLAYER_SELF("essentailss.teleport.request.to.self", true),
    TELEPORT_REQUEST_TO_PLAYER_OTHER("essentailss.teleport.request.to.other"),
    TELEPORT_REQUEST_HERE_SELF("essentialss.teleport.request.here.self", true),
    TELEPORT_REQUEST_HERE_OTHER("essentialss.teleport.request.here.other"),
    SPAWN_CREATE_DISTANCE("essentialss.spawn.create.distance"),
    SPAWN_CREATE_FIRST("essentialss.spawn.create.first"),
    SPAWN_CREATE_MAIN("essentialss.spawn.create.main"),
    SPAWN_TELEPORT_SELF("essentialss.spawn.self"),
    SPAWN_TELEPORT_OTHER("essentialss.spawn.other"),
    SPAWNS("essentialss.spawns"),
    HAT_SELF("essentialss.hat.self", true),
    HAT_OTHER("essentialss.hat.other"),
    HAT_CREATE_ITEM("essentialss.hat.item"),
    BAN_BY_ALL("essentialss.ban.by"),
    BAN_BY_ACCOUNT("essentialss.ban.by.account"),
    BAN_BY_IP("essentialss.ban.by.ip"),
    BAN_BY_IP_PLAYER("essentialss.ban.by.ip.player"),
    BAN_BY_MAC_ADDRESS("essentialss.ban.by.mac"),
    INVENTORY_SEE_READ("essentialss.inv.see.read"),
    INVENTORY_SEE_WRITE("essentialss.inv.see.write"),
    CRAFTING_SELF("essentialss.inv.crafting.self"),
    CRAFTING_OTHER("essentailss.inv.crafting.other"),
    FURNACE_SELF("essentialss.inv.furnace.self"),
    FURNACE_OTHER("essentialss.inv.furnace.other"),
    BLAST_FURNACE_SELF("essentialss.inv.blastfurnace.self"),
    BLAST_FURNACE_OTHER("essentialss.inv.blastfurnace.other"),
    ANVIL_SELF("essentialss.inv.anvil.self"),
    ANVIL_OTHER("essentialss.inv.anvil.other"),
    ESSENTIALSS_PERFORMANCE("essentialss.performance"),
    ESSENTIALSS_PLUGINS("essentialss.plugins"),
    GOD_MODE_SELF("essentialss.god.self"),
    GOD_MODE_OTHER("essentialss.god.other"),
    DEMI_GOD_MODE_SELF("essentialss.demigod.self"),
    DEMI_GOD_MODE_OTHER("essentialss.demigod.other"),
    LOAD_WORLD("essentialss.world.load"),
    UNLOAD_WORLD("essentialss.world.unload"),
    CREATE_WORLD("essentialss.world.create"),
    LIST_WORLDS("essentialss.worlds.list"),
    FEED_SELF("essentialss.feed.self"),
    FEED_OTHER("essentialss.feed.other"),
    BREW_SELF("essentialss.inv.anvil.self"),
    BREW_OTHER("essentialss.inv.anvil.other");

    private final @NotNull String node;
    private final boolean shouldApplyToDefault;

    SPermissions(@NotNull String node) {
        this(node, false);
    }

    SPermissions(@NotNull String node, boolean shouldApplyToDefault) {
        this.node = node;
        this.shouldApplyToDefault = shouldApplyToDefault;
    }

    @Override
    public String node() {
        return this.node;
    }

    @Override
    public boolean shouldApplyToDefault() {
        return this.shouldApplyToDefault;
    }

    public static SPermissions getPermissionForGamemode(@SuppressWarnings("TypeMayBeWeakened") @NotNull GameMode gamemode) {
        if (gamemode.equals(GameModes.SURVIVAL.get())) {
            return GAMEMODE_SURVIVAL;
        }
        if (gamemode.equals(GameModes.CREATIVE.get())) {
            return GAMEMODE_CREATIVE;
        }
        throw new IllegalStateException("No permission found for gamemode of " + gamemode.key(RegistryTypes.GAME_MODE));
    }
}
