package org.essentialss.command.teleport.request;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.PlayerTeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Comparator;
import java.util.Optional;

public final class TeleportAcceptRequestCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> holder;
        private final @NotNull Parameter.Value<SGeneralPlayerData> sender;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> holder,
                        @NotNull Parameter.Value<SGeneralPlayerData> sender) {
            this.holder = holder;
            this.sender = sender;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<SGeneralPlayerData> opHolder = context.one(this.holder);
            if (!opHolder.isPresent()) {
                if (context.subject() instanceof Player) {
                    opHolder = Optional.of(
                            EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject()));
                }
            }
            if (!opHolder.isPresent()) {
                return CommandResult.error(Component.text("Both players must be specified"));
            }

            Optional<SGeneralPlayerData> opSender = context.one(this.sender);
            if (!opSender.isPresent()) {
                UnmodifiableCollection<PlayerTeleportRequest> requests = opHolder
                        .get()
                        .teleportRequests(PlayerTeleportRequest.class);
                opSender = requests
                        .stream()
                        .min(Comparator.comparing(TeleportRequest::sentTime))
                        .flatMap(PlayerTeleportRequest::senderAsPlayerData);
            }

            if (!opSender.isPresent()) {
                return CommandResult.error(Component.text("Both players must be specified"));
            }
            return TeleportAcceptRequestCommand.execute(opHolder.get(), opSender.get());
        }
    }

    private TeleportAcceptRequestCommand() {
        throw new RuntimeException("Should not create");
    }

    public static CommandResult execute(@NotNull SGeneralPlayerData host, @NotNull SGeneralPlayerData playerData) {
        PlayerTeleportRequest request = host.acceptTeleportRequest(playerData.uuid());
        Component messageHolder = null;
        Component messageSender = null;
        switch (request.directionOfTeleport()) {
            case TOWARDS_REQUEST_HOLDER:
                messageHolder = playerData.displayName().append(Component.text(" teleported to you"));
                messageSender = Component.text("You teleported to ").append(host.displayName());
                break;
            case TOWARDS_REQUEST_SENDER:
                messageHolder = Component.text("You teleported to ").append(playerData.displayName());
                messageSender = host.displayName().append(Component.text(" teleported to you"));
                break;
        }
        host.spongePlayer().sendMessage(messageHolder);
        playerData.spongePlayer().sendMessage(messageSender);
        return CommandResult.success();
    }

    public static Command.Parameterized createTeleportAcceptCommand() {
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
            return playerData
                    .teleportRequests(PlayerTeleportRequest.class)
                    .parallelStream()
                    .anyMatch(pd -> pd.sender().equals(data.uuid()));
        }).optional().key("player").build();

        return Command
                .builder()
                .addParameter(sender)
                .addParameter(holder)
                .executor(new Execute(holder, sender))
                .build();

    }
}
