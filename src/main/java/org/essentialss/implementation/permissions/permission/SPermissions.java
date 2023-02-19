package org.essentialss.implementation.permissions.permission;

import org.jetbrains.annotations.NotNull;

public enum SPermissions implements SPermission {
    WARP_CREATE("essentialss.warp.create"),
    WARP_TELEPORT_SELF("essentialss.warp.teleport.self"),
    WARP_TELEPORT_OTHER("essentialsss.warp.teleport.other"),
    WARP_REMOVE("essentialss.warp.remove"),
    WARPS("essentialss.warps"),
    SPAWN_CREATE_DISTANCE("essentialss.spawn.create.distance"),
    SPAWN_CREATE_FIRST("essentialss.spawn.create.first"),
    SPAWN_CREATE_MAIN("essentialss.spawn.create.main"),
    SPAWN_TELEPORT_SELF("essentialss.spawn.self"),
    SPAWN_TELEPORT_OTHER("essentialss.spawn.other"),
    SPAWNS("essentialss.spawns"),
    HAT_SELF("essentials.hat.self"),
    HAT_OTHER("essentials.hat.other"),
    HAT_CREATE_ITEM("essentials.hat.item");

    private final @NotNull String node;
    private final boolean shouldApplyToDefault;

    private SPermissions(@NotNull String node) {
        this(node, false);
    }

    private SPermissions(@NotNull String node, boolean shouldApplyToDefault) {
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
