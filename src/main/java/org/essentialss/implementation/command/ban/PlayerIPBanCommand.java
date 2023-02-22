package org.essentialss.implementation.command.ban;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.BanConfig;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.LocalDateTime;

public final class PlayerIPBanCommand {


    private static final class PlayerExecute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> user;

        private PlayerExecute(@NotNull Parameter.Value<SGeneralPlayerData> user) {
            this.user = user;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = context.requireOne(this.user);
            Player spongePlayer = player.spongePlayer();
            if (!(spongePlayer instanceof ServerPlayer)) {
                throw new CommandException(Component.text("Server only command"));
            }
            ServerPlayer serverPlayer = (ServerPlayer) spongePlayer;
            return PlayerIPBanCommand.execute(serverPlayer.connection().address().getHostName(), player.playerName(), null);
        }
    }

    private PlayerIPBanCommand() {
        throw new RuntimeException("Should not create");
    }

    static Command.Parameterized createIPAddressBanCommand() {
        Parameter.Value<SGeneralPlayerData> parameter = SParameters.onlinePlayer(t -> true).key("player").build();

        return Command
                .builder()
                .addParameter(parameter)
                .executor(new PlayerExecute(parameter))
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .permission(SPermissions.BAN_BY_MAC_ADDRESS.node())
                .build();
    }

    public static CommandResult execute(@NotNull String hostname, @Nullable String lastKnownUser, @Nullable LocalDateTime until) {
        EssentialsSMain.plugin().banManager().get().banIp(hostname, lastKnownUser, until);
        BanConfig banConfig = EssentialsSMain.plugin().banManager().get().banConfig().get();
        if (Sponge.isServerAvailable()) {
            for (ServerPlayer player : Sponge.server().onlinePlayers()) {
                String playersHostname = player.connection().address().getHostName();
                if (playersHostname.equalsIgnoreCase(hostname)) {
                    player.kick(banConfig.banMessage().parseDefault(banConfig));
                }
            }
        }
        return CommandResult.success();
    }

}
