package org.essentialss.command.point.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;

public final class TeleportToHomeCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SHome> homes;
        private final @NotNull Parameter.Value<ServerPlayer> target;

        private Execute(@NotNull Parameter.Value<SHome> home, @NotNull Parameter.Value<ServerPlayer> target) {
            this.homes = home;
            this.target = target;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerPlayer> opPlayer = context.one(this.target);
            SGeneralPlayerData playerData;
            if (opPlayer.isPresent()) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor(opPlayer.get());
            } else if (context.subject() instanceof Player) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                throw new CommandException(Component.text("Player needs to be specified"));
            }

            Optional<SHome> opHome = context.one(this.homes);
            SHome home;
            if (opHome.isPresent()) {
                home = opHome.get();
            } else {
                UnmodifiableCollection<SHome> homes = playerData.homes();
                if (homes.isEmpty()) {
                    throw new CommandException(Component.text("No homes to teleport to").color(NamedTextColor.RED));
                }
                home = homes
                        .stream()
                        .min(Comparator.comparing(playerHome -> playerHome.position().distance(playerData.spongePlayer().position())))
                        .orElseThrow(() -> new CommandException(Component.text("No homes to teleport to").color(NamedTextColor.RED)));
            }

            return TeleportToHomeCommand.execute(playerData, home);
        }
    }

    private TeleportToHomeCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createHomeToCommand() {
        return createHomeToCommand(Command.builder());
    }

    public static Command.Parameterized createHomeToCommand(@NotNull Command.Parameterized.Builder builder) {
        Parameter.Value<ServerPlayer> player = Parameter.player().key("player").requiredPermission(SPermissions.HOME_TELEPORT_OTHER.node()).optional().build();
        Parameter.Value<SHome> home = SParameters.home(context -> {
            Optional<ServerPlayer> opPlayer = context.one(player);
            if (opPlayer.isPresent()) {
                return EssentialsSMain.plugin().playerManager().get().dataFor(opPlayer.get());
            } else if (context.subject() instanceof Player) {
                return EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                //noinspection ReturnOfNull
                return null;
            }

        }).optional().key("home").build();

        return builder.addParameter(home).addParameter(player).executor(new Execute(home, player)).permission(SPermissions.HOME_TELEPORT_SELF.node()).build();
    }

    public static CommandResult execute(@NotNull SGeneralPlayerData playerData, @NotNull SPoint home) {
        try {
            playerData.teleport(home.location());
            return CommandResult.success();
        } catch (IllegalStateException e) {
            return CommandResult.error(Component.text("The world you are trying to travel to is not loaded"));
        }
    }

}
