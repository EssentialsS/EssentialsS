package org.essentialss.implementation.command.point.teleport;

import net.kyori.adventure.text.Component;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
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

public class TeleportToSpawnCommand {


    private static class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<ServerPlayer> target;

        private Execute(@NotNull Parameter.Value<ServerPlayer> target) {
            this.target = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerPlayer> opPlayer = context.one(this.target);
            if (opPlayer.isPresent()) {
                return TeleportToSpawnCommand.execute(opPlayer.get());
            }
            if (context.subject() instanceof Player) {
                return TeleportToSpawnCommand.execute((Player) context.subject());
            }
            throw new CommandException(Component.text("Player needs to be specified"));
        }
    }

    public static CommandResult execute(@NotNull Player player) {
        SSpawnPoint point = EssentialsSMain
                .plugin()
                .worldManager()
                .get()
                .dataFor(player.world())
                .spawnPoint(player.location().position());
        EssentialsSMain.plugin().playerManager().get().dataFor(player).teleport(point.position());
        return CommandResult.success();
    }

    public static Command.Parameterized createSpawnToCommand() {
        return createSpawnToCommand(Command.builder());
    }

    public static Command.Parameterized createSpawnToCommand(@NotNull Command.Parameterized.Builder builder) {
        Parameter.Value<ServerPlayer> player = Parameter.player().key("player").optional().build();
        return builder.addParameter(player).executor(new Execute(player)).build();
    }

}
