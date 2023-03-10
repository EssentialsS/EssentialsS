package org.essentialss.implementation.permissions.permission;

import org.jetbrains.annotations.NotNull;

public enum SPermissions implements SPermission {

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
    INVENTORY_SEE("essentialss.inv.see"),
    CRAFTING_SELF("essentialss.inv.crafting.self"),
    CRAFTING_OTHER("essentailss.inv.crafting.other"),
    FURNACE_SELF("essentialss.inv.furnace.self"),
    FURNACE_OTHER("essentialss.inv.furnace.other"),
    BLAST_FURNACE_SELF("essentialss.inv.blastfurnace.self"),
    BLAST_FURNACE_OTHER("essentialss.inv.blastfurnace.other"),

    ANVIL_SELF("essentialss.inv.anvil.self"),
    ANVIL_OTHER("essentialss.inv.anvil.other"),
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
}
