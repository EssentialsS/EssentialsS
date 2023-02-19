package org.essentialss.implementation.command.unban;

import org.spongepowered.api.command.Command;

public class UnbanCommands {

    public static Command.Parameterized createUnbanCommands() {
        Command.Parameterized accountUnban = AccountUnbanCommand.createAccountUnbanCommand();
        return Command.builder().addChild(accountUnban, "account").build();
    }
}
