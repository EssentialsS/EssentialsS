package org.essentialss.command.point.delete;

import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.Cause;

public final class DeleteWarpCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SWarp> warp;

        private Execute(@NotNull Parameter.Value<SWarp> warp) {
            this.warp = warp;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SWarp warp = context.requireOne(this.warp);
            return DeleteWarpCommand.execute(warp, context.contextCause());
        }
    }

    private DeleteWarpCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createDeleteWarpCommand() {
        Parameter.Value<SWarp> warp = SParameters.warp().key("warp").build();
        return Command.builder().addParameter(warp).executor(new Execute(warp)).permission(SPermissions.WARP_REMOVE.node()).build();
    }

    public static CommandResult execute(@NotNull SWarp warp, @NotNull Cause cause) {
        boolean deregistered = warp.worldData().deregister(warp, cause);
        return deregistered ? CommandResult.success() : CommandResult.error(Component.text("Could not delete warp"));
    }

}
