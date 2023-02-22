package org.essentialss.implementation.command.teleport.request;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.PlayerTeleportRequest;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class TeleportDenyRequestCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> holder;
        private final @NotNull Parameter.Value<SGeneralPlayerData> sender;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> holder, @NotNull Parameter.Value<SGeneralPlayerData> sender) {
            this.holder = holder;
            this.sender = sender;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData sender = context.requireOne(this.sender);
            Optional<SGeneralPlayerData> opHolder = context.one(this.holder);
            if (!opHolder.isPresent()) {
                if (context.subject() instanceof Player) {
                    opHolder = Optional.of(EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject()));
                } else {
                    return CommandResult.error(Component.text("Both players must be specified"));
                }
            }
            return TeleportDenyRequestCommand.execute(opHolder.get(), sender);
        }
    }

    private TeleportDenyRequestCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createTeleportDenyCommand() {
        Parameter.Value<SGeneralPlayerData> holder = SParameters
                .onlinePlayer(p -> !p.teleportRequests(PlayerTeleportRequest.class).isEmpty())
                .optional()
                .key("target")
                .build();
        Parameter.Value<SGeneralPlayerData> sender = SParameters.onlinePlayer(p -> true, (context, data) -> {
            Optional<SGeneralPlayerData> opData = context.one(holder);
            SGeneralPlayerData playerData;
            if (opData.isPresent()) {
                playerData = opData.get();
            } else if (context.subject() instanceof Player) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                return false;
            }
            return playerData.teleportRequests(PlayerTeleportRequest.class).parallelStream().anyMatch(pd -> pd.sender().equals(data.uuid()));
        }).key("player").build();

        return Command.builder().addParameter(holder).addParameter(sender).executor(new Execute(holder, sender)).build();

    }

    public static CommandResult execute(@NotNull SGeneralPlayerData host, @NotNull SGeneralPlayerData playerData) {
        host.declineTeleportRequest(playerData.uuid());
        Component messageSender = host.displayName().append(Component.text(" has denied your teleport request"));
        host.spongePlayer().sendMessage(messageSender);
        playerData.spongePlayer().sendMessage(messageSender);
        return CommandResult.success();
    }
}
