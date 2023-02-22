package org.essentialss.implementation.command.unban;

import org.spongepowered.api.command.Command;

public final class UnbanCommands {

    private UnbanCommands() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createUnbanCommands() {
        Command.Parameterized accountUnban = AccountUnbanCommand.createAccountUnbanCommand();
        return Command.builder().addChild(accountUnban, "account").build();
    }
}
