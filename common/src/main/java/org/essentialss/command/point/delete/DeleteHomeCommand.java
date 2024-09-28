package org.essentialss.command.point.delete;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Optional;

public final class DeleteHomeCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SHome> warp;
        private final @NotNull Parameter.Value<SGeneralUnloadedData> playerData;

        private Execute(@NotNull Parameter.Value<SHome> warp, @NotNull Parameter.Value<SGeneralUnloadedData> playerData) {
            this.warp = warp;
            this.playerData = playerData;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<SGeneralUnloadedData> opPlayerData = context.one(this.playerData);
            SGeneralUnloadedData playerData;
            if (opPlayerData.isPresent()) {
                playerData = opPlayerData.get();
            } else if (context.subject() instanceof Player) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                throw new CommandException(EssentialsSMain.plugin().messageManager().get().adapters().playerOnlyCommand().get().adaptMessage());
            }
            SHome warp = context.requireOne(this.warp);
            return DeleteHomeCommand.execute(playerData, warp);
        }
    }

    private DeleteHomeCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createDeleteHomeCommand() {
        Parameter.Value<SGeneralUnloadedData> playerData = SParameters
                .offlinePlayersNickname(false, general -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.HOME_REMOVE_OTHER.node())
                .build();
        Parameter.Value<SHome> home = SParameters.home(context -> {
            Optional<SGeneralUnloadedData> opPlayerData = context.one(playerData);
            return opPlayerData.orElse(null);
        }).key("home").build();
        return Command.builder().addParameter(home).executor(new Execute(home, playerData)).permission(SPermissions.HOME_REMOVE_SELF.node()).build();
    }

    public static CommandResult execute(@NotNull SGeneralUnloadedData playerData, @NotNull SHome warp) {
        playerData.deregister(warp);
        try {
            playerData.saveToConfig();
            return CommandResult.success();
        } catch (ConfigurateException e) {
            return CommandResult.error(Component.text("Could not delete home"));
        }
    }

}
