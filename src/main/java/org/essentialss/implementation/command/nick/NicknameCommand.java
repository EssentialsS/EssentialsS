package org.essentialss.implementation.command.nick;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

public class NicknameCommand {

    private static class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<Component> newNickname;
        private final @NotNull Parameter.Value<ServerPlayer> target;

        private Execute(@NotNull Parameter.Value<ServerPlayer> player,
                        @NotNull Parameter.Value<Component> newNickname) {
            this.newNickname = newNickname;
            this.target = player;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<Player> opPlayer = context.one(this.target).map(player -> player);
            if (!opPlayer.isPresent() && (context.subject() instanceof Player)) {
                opPlayer = Optional.of((Player) context.subject());
            }
            Player player = opPlayer.orElseThrow(
                    () -> new CommandException(Component.text("Player must be specified")));
            Component component = context.requireOne(this.newNickname);
            return NicknameCommand.execute(player, component);
        }
    }

    public static CommandResult execute(@NotNull Player player, @NotNull Component component) {
        player.displayName().set(component);
        return CommandResult.success();
    }

    public static Command.Parameterized createNicknameCommand() {
        Parameter.Value<ServerPlayer> player = Parameter.playerOrTarget().key("target").optional().build();
        Parameter.Value<Component> message = Parameter.formattingCodeText().key("nickname").build();

        return Command
                .builder()
                .executor(new Execute(player, message))
                .addParameter(message)
                .addParameter(player)
                .build();
    }

}
