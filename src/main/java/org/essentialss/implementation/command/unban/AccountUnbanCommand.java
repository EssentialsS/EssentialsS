package org.essentialss.implementation.command.unban;

import net.kyori.adventure.text.Component;
import org.essentialss.api.ban.SBanManager;
import org.essentialss.api.ban.data.AccountBanData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Optional;
import java.util.UUID;

public class AccountUnbanCommand {

    public static class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralUnloadedData> player;

        public Execute(@NotNull Parameter.Value<SGeneralUnloadedData> player) {
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return AccountUnbanCommand.execute(context.requireOne(this.player).uuid());
        }
    }

    public static CommandResult execute(@NotNull UUID uuid) {
        SBanManager banManager = EssentialsSMain.plugin().banManager().get();
        Optional<AccountBanData> opAccountBan = banManager
                .banData(AccountBanData.class)
                .parallelStream()
                .filter(accountBanData -> accountBanData.profile().uuid().equals(uuid))
                .findAny();
        if (!opAccountBan.isPresent()) {
            return CommandResult.error(Component.text("Account is not banned"));
        }
        AccountBanData accountBan = opAccountBan.get();
        banManager.unban(accountBan);
        return CommandResult.success();
    }

    public static Command.Parameterized createAccountUnbanCommand() {
        Parameter.Value<SGeneralUnloadedData> accountParameter = SParameters.offlinePlayersNicknames(false, player -> {
            SBanManager banManager = EssentialsSMain.plugin().banManager().get();
            return banManager
                    .banData(AccountBanData.class)
                    .stream()
                    .anyMatch(banData -> banData.profile().uuid().equals(player.uuid()));
        }).key("account").build();


        return Command.builder().addParameter(accountParameter).executor(new Execute(accountParameter)).build();
    }
}
