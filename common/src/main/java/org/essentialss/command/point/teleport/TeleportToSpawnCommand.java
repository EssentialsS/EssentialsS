package org.essentialss.command.point.teleport;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.events.player.teleport.PlayerTeleportToPointImpl;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;

import java.util.Optional;

public final class TeleportToSpawnCommand {


    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<ServerPlayer> target;

        private Execute(@NotNull Parameter.Value<ServerPlayer> target) {
            this.target = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerPlayer> opPlayer = context.one(this.target);
            if (opPlayer.isPresent()) {
                return TeleportToSpawnCommand.execute(opPlayer.get(), context.contextCause());
            }
            if (context.subject() instanceof Player) {
                return TeleportToSpawnCommand.execute((Player) context.subject(), context.contextCause());
            }
            throw new CommandException(Component.text("Player needs to be specified"));
        }
    }

    private TeleportToSpawnCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createSpawnToCommand() {
        return createSpawnToCommand(Command.builder());
    }

    public static Command.Parameterized createSpawnToCommand(@NotNull Command.Parameterized.Builder builder) {
        Parameter.Value<ServerPlayer> player = Parameter.player().key("player").requiredPermission(SPermissions.SPAWN_TELEPORT_OTHER.node()).optional().build();
        return builder.addParameter(player).executor(new Execute(player)).permission(SPermissions.SPAWN_TELEPORT_SELF.node()).build();
    }

    public static CommandResult execute(@NotNull Player player, @NotNull Cause cause) {
        SSpawnPoint point = EssentialsSMain.plugin().worldManager().get().dataFor(player.world()).spawnPoint(player.location().position());

        PlayerTeleportToPointImpl event = new PlayerTeleportToPointImpl(player, point, cause);
        Sponge.eventManager().post(event);
        if (event.isCancelled()) {
            return CommandResult.error(Component.text("Was cancelled by another plugin"));
        }

        EssentialsSMain.plugin().playerManager().get().dataFor(player).teleport(point.position());
        return CommandResult.success();
    }

}
