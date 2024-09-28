package org.essentialss.command.teleport.request;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.SParameters;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public final class TeleportRequestHerePlayerCommand {

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

            return TeleportRequestHerePlayerCommand.execute(opRequestingPlayer.get(), receivingPlayer);
        }
    }

    private TeleportRequestHerePlayerCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createTeleportRequestHerePlayerCommand() {
        Parameter.Value<SGeneralPlayerData> receivingPlayer = SParameters.onlinePlayer(p -> true).key("here").build();
        Parameter.Value<SGeneralPlayerData> sender = SParameters
                .onlinePlayer(p -> true)
                .key("sender")
                .requiredPermission(SPermissions.TELEPORT_REQUEST_TO_PLAYER_OTHER.node())
                .optional()
                .build();

        return Command.builder().addParameter(sender).addParameter(receivingPlayer).executor(new Execute(sender, receivingPlayer)).build();
    }

    private static CommandResult execute(@NotNull SGeneralPlayerData playerRequesting, @NotNull SGeneralPlayerData playerReceiving) {
        TeleportRequestBuilder teleportRequestBuilder = new TeleportRequestBuilder().requestTowardsSender(playerRequesting.spongePlayer());
        playerReceiving.register(teleportRequestBuilder);
        playerReceiving.spongePlayer().sendMessage(playerRequesting.displayName().append(Component.text(" has requested you to teleport to them")));
        playerRequesting.spongePlayer().sendMessage(Component.text("Request to teleport here has been sent to ").append(playerReceiving.displayName()));
        return CommandResult.success();
    }

}
