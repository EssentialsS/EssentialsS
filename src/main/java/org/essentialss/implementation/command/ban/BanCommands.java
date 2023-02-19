package org.essentialss.implementation.command.ban;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;

public class BanCommands {

    public static Command.Parameterized createBanCommand() {
        Command.Parameterized banAccountCommand = AccountBanCommand.createBanAccountCommand();
        Command.Parameterized ipAddressCommand = IPBanCommand.createIPAddressBanCommand();
        Command.Parameterized playerIpAddressCommand = PlayerIPBanCommand.createIPAddressBanCommand();

        return Command
                .builder()
                .addChild(banAccountCommand, "account", "playeraccount")
                .addChild(ipAddressCommand, "ip", "ipAddress", "ipv4", "ipv6", "hostname")
                .addChild(playerIpAddressCommand, "playerip", "playersip")
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .build();
    }
}
