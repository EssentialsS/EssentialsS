package org.essentialss.implementation.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public class RunCommand {

    public static class Execute implements CommandExecutor {

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return null;
        }
    }


    public static @NotNull Command.Parameterized createRun(){

    }
}
