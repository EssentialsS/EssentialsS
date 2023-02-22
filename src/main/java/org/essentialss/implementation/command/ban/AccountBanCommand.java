package org.essentialss.implementation.command.ban;

import net.kyori.adventure.text.Component;
import org.essentialss.api.ban.SBanManager;
import org.essentialss.api.player.data.SGeneralUnloadedData;
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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurateException;

import java.time.LocalDateTime;
import java.util.Optional;

public final class AccountBanCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralUnloadedData> offlinePlayer;
        private final @NotNull Parameter.Value<Component> reason;

        private Execute(@NotNull Parameter.Value<SGeneralUnloadedData> offlinePlayer, @NotNull Parameter.Value<Component> reason) {
            this.offlinePlayer = offlinePlayer;
            this.reason = reason;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            GameProfile profile = context
                    .requireOne(this.offlinePlayer)
                    .profile()
                    .orElseThrow(() -> new CommandException(Component.text("Command is for server use only")));
            Optional<Component> opReason = context.one(this.reason);
            return AccountBanCommand.execute(profile, null, opReason.orElse(null));
        }
    }

    private AccountBanCommand() {
        throw new RuntimeException("Should not create");
    }

    static Command.Parameterized createBanAccountCommand() {
        Parameter.Value<SGeneralUnloadedData> profile = SParameters.offlinePlayersNickname(false, t -> true).key("player").build();
        Parameter.Value<Component> reason = Parameter.formattingCodeTextOfRemainingElements().optional().key("reason").build();

        return Command
                .builder()
                .addParameter(profile)
                .addParameter(reason)
                .executor(new Execute(profile, reason))
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .build();
    }

    public static CommandResult execute(@NotNull GameProfile profileToBan, @Nullable LocalDateTime until, @Nullable Component reason) {
        SBanManager banManager = EssentialsSMain.plugin().banManager().get();
        banManager.banAccount(profileToBan, until);
        try {
            banManager.saveToConfig();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        if (!Sponge.isServerAvailable()) {
            return CommandResult.success();
        }

        Optional<ServerPlayer> opBanningPlayer = Sponge
                .server()
                .onlinePlayers()
                .parallelStream()
                .filter(player -> player.profile().equals(profileToBan))
                .findAny();
        if (!opBanningPlayer.isPresent()) {
            return CommandResult.success();
        }

        if (null == reason) {
            opBanningPlayer.get().kick();
        } else {
            opBanningPlayer.get().kick(reason);
        }
        return CommandResult.success();
    }
}
