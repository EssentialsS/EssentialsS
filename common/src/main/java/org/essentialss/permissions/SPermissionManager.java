package org.essentialss.permissions;

import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;

public class SPermissionManager {

    public boolean isPermissionsPluginInstalled() {
        return Sponge.serviceProvider().registration(PermissionService.class).isPresent();
    }

    public @NotNull SPermissions[] permissions() {
        return SPermissions.values();
    }

}
