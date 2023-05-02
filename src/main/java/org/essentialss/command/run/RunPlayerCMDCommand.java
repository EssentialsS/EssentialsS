package org.essentialss.command.run;

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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

public final class RunPlayerCMDCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Value<String> commandParameter;
        private final @NotNull Value<ServerPlayer> playerParameter;

        private Execute(@NotNull Value<ServerPlayer> playerParameter, @NotNull Value<String> commandParameter) {
            this.commandParameter = commandParameter;
            this.playerParameter = playerParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            String command = context.requireOne(this.commandParameter);
            ServerPlayer player = context.requireOne(this.playerParameter);
            return RunPlayerCMDCommand.execute(player, context.cause().audience(), command);
        }
    }

    private RunPlayerCMDCommand() {
        throw new RuntimeException("Should not create");
    }

    static @NotNull Command.Parameterized createRunPlayerCommand() {
        Parameter.Key<ServerPlayer> serverPlayerKey = Parameter.key("player", ServerPlayer.class);
        Value<ServerPlayer> playerParameter = Parameter.player().key(serverPlayerKey).build();

        Parameter.Key<String> commandParameterKey = Parameter.key("command", String.class);
        Value<String> commandParameter = SParameters
                .commandParameter((cause, input) -> cause.requireOne(serverPlayerKey), (cause, input) -> cause.cause().audience())
                .key(commandParameterKey)
                .build();

        return Command
                .builder()
                .executor(new Execute(playerParameter, commandParameter))
                .addParameter(playerParameter)
                .addParameter(commandParameter)
                .executionRequirements(cause -> Sponge.isServerAvailable())
                .build();
    }

    public static CommandResult execute(@NotNull Subject player, @NotNull Audience audience, @NotNull String command) throws CommandException {
        return Sponge.server().commandManager().process(player, audience, command);
    }
}
