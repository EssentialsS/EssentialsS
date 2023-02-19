package org.essentialss.implementation.command.ban;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
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

public class IPBanCommand {

    private static class PlayerExecute implements CommandExecutor {

        private final @NotNull Parameter.Value<String> hostname;

        private PlayerExecute(Parameter.Value<String> user) {
            this.hostname = user;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            String hostname = context.requireOne(this.hostname);
            return IPBanCommand.execute(hostname, null, null);
        }
    }

    public static CommandResult execute(@NotNull String hostname,
                                        @Nullable String lastKnownUser,
                                        @Nullable LocalDateTime until) {
        EssentialsSMain.plugin().banManager().get().banIp(hostname, lastKnownUser, until);
        return CommandResult.success();
    }

    public static Command.Parameterized createIPAddressBanCommand() {
        Parameter.Value<String> parameter = SParameters.hostname().key("ipAddress").build();

        return Command
                .builder()
                .addParameter(parameter)
                .executor(new PlayerExecute(parameter))
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .build();
    }

}
