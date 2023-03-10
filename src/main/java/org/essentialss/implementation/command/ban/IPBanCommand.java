package org.essentialss.implementation.command.ban;

import org.essentialss.api.config.configs.BanConfig;
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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.LocalDateTime;

public final class IPBanCommand {

    private static final class PlayerExecute implements CommandExecutor {

        private final @NotNull Parameter.Value<String> hostname;

        private PlayerExecute(@NotNull Parameter.Value<String> user) {
            this.hostname = user;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            String hostname = context.requireOne(this.hostname);
            return IPBanCommand.execute(hostname, null, null);
        }
    }

    private IPBanCommand() {
        throw new RuntimeException("Should not create");
    }

    static Command.Parameterized createIPAddressBanCommand() {
        Parameter.Value<String> parameter = SParameters.hostname().key("ipAddress").build();

        return Command
                .builder()
                .addParameter(parameter)
                .executor(new PlayerExecute(parameter))
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .permission(SPermissions.BAN_BY_IP.node())
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
