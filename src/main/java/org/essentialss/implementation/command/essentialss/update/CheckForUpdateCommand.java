package org.essentialss.implementation.command.essentialss.update;

import org.essentialss.implementation.schedules.UpdateCheck;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

public final class CheckForUpdateCommand {

    private static final class Execute implements CommandExecutor {

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            UpdateCheck.createDelay(context.cause().audience());
            return CommandResult.success();
        }
    }

    private CheckForUpdateCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createUpdateCheckCommand() {
        return Command.builder().executor(new Execute()).build();
    }
}
