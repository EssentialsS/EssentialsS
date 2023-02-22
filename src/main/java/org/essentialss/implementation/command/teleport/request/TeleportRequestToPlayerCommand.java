package org.essentialss.implementation.command.teleport.request;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class TeleportRequestToPlayerCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<SGeneralPlayerData> playerRequesting;
        private final @NotNull Parameter.Value<SGeneralPlayerData> playerReceiving;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> playerRequesting, @NotNull Parameter.Value<SGeneralPlayerData> playerReceiving) {
            this.playerReceiving = playerReceiving;
            this.playerRequesting = playerRequesting;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData receivingPlayer = context.requireOne(this.playerReceiving);
            Optional<SGeneralPlayerData> opRequestingPlayer = context.one(this.playerRequesting);
            if (!(opRequestingPlayer.isPresent())) {
                if (!(context.subject() instanceof Player)) {
                    return CommandResult.error(Component.text("Both players need to be specified"));
                }
                Player player = (Player) context.subject();
                SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
                opRequestingPlayer = Optional.of(playerData);
            }

            return TeleportRequestToPlayerCommand.execute(opRequestingPlayer.get(), receivingPlayer);
        }
    }

    private TeleportRequestToPlayerCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createTeleportRequestToPlayerCommand() {
        Parameter.Value<SGeneralPlayerData> receivingPlayer = SParameters.onlinePlayer(p -> true).key("to").build();
        Parameter.Value<SGeneralPlayerData> sender = SParameters
                .onlinePlayer(p -> true)
                .key("sender")
                .requiredPermission(SPermissions.TELEPORT_REQUEST_TO_PLAYER_OTHER.node())
                .optional()
                .build();

        return Command.builder().addParameter(sender).addParameter(receivingPlayer).executor(new Execute(sender, receivingPlayer)).build();
    }

    private static CommandResult execute(@NotNull SGeneralPlayerData playerRequesting, @NotNull SGeneralPlayerData playerReceiving) {
        TeleportRequestBuilder teleportRequestBuilder = new TeleportRequestBuilder().requestTowardsHolder(playerRequesting.spongePlayer());
        playerReceiving.register(teleportRequestBuilder);
        playerReceiving.spongePlayer().sendMessage(playerRequesting.displayName().append(Component.text(" has requested to teleport to you")));
        playerRequesting
                .spongePlayer()
                .sendMessage(Component.text("Request to teleport to ").append(playerReceiving.displayName()).append(Component.text(" has been sent")));
        return CommandResult.success();
    }

}
