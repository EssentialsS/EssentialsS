package org.essentialss.implementation.command.run;

import net.kyori.adventure.audience.Audience;
import org.essentialss.api.utils.SParameters;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.Parameter.Value;

public final class RunConsoleCMDCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Value<String> commandParameter;

        private Execute(@NotNull Value<String> parameter) {
            this.commandParameter = parameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            String command = context.requireOne(this.commandParameter);
            return RunConsoleCMDCommand.execute(context.cause().audience(), command);
        }
    }

    private RunConsoleCMDCommand() {
        throw new RuntimeException("Should not create");
    }

    public static @NotNull Command.Parameterized createRunConsoleCommand() {
        Parameter.Key<String> commandParameterKey = Parameter.key("command", String.class);
        Value<String> commandParameter = SParameters
                .commandParameter((cause, input) -> Sponge.systemSubject(), (cause, input) -> cause.cause().audience())
                .key(commandParameterKey)
                .build();

        return Command
                .builder()
                .executor(new Execute(commandParameter))
                .addParameter(commandParameter)
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull String command) throws CommandException {
        return Sponge.server().commandManager().process(Sponge.systemSubject(), audience, command);
    }
}
