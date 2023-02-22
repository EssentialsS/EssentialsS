package org.essentialss.implementation.command.point.teleport;

import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

public final class TeleportToWarpCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SWarp> warps;
        private final @NotNull Parameter.Value<ServerPlayer> target;

        private Execute(@NotNull Parameter.Value<SWarp> warp, @NotNull Parameter.Value<ServerPlayer> target) {
            this.warps = warp;
            this.target = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SWarp warp = context.requireOne(this.warps);
            Optional<ServerPlayer> opPlayer = context.one(this.target);
            if (opPlayer.isPresent()) {
                return TeleportToWarpCommand.execute(opPlayer.get(), warp);
            }
            if (context.subject() instanceof Player) {
                return TeleportToWarpCommand.execute((Player) context.subject(), warp);
            }
            throw new CommandException(Component.text("Player needs to be specified"));
        }
    }

    private TeleportToWarpCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createWarpToCommand() {
        return createWarpToCommand(Command.builder());
    }

    public static Command.Parameterized createWarpToCommand(@NotNull Command.Parameterized.Builder builder) {
        Parameter.Value<ServerPlayer> player = Parameter.player().key("player").requiredPermission(SPermissions.WARP_TELEPORT_OTHER.node()).optional().build();
        Parameter.Value<SWarp> warp = SParameters.warp().key("warp").build();

        return builder.addParameter(warp).addParameter(player).executor(new Execute(warp, player)).permission(SPermissions.WARP_TELEPORT_SELF.node()).build();
    }

    public static CommandResult execute(@NotNull Player player, @NotNull SPoint warp) {
        try {
            EssentialsSMain.plugin().playerManager().get().dataFor(player).teleport(warp.location());
            return CommandResult.success();
        } catch (IllegalStateException e) {
            return CommandResult.error(Component.text("The world you are trying to warp to is not loaded"));
        }
    }

}
