package org.essentialss.implementation.permissions.permission;

import org.jetbrains.annotations.NotNull;

public enum SPermissions implements SPermission {
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
    HAT_SELF("essentials.hat.self", true),
    HAT_OTHER("essentials.hat.other"),
    HAT_CREATE_ITEM("essentials.hat.item");

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
