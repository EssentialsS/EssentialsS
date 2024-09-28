package org.essentialss.command.gamemode;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.permission.Subject;

public final class GamemodeCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> player;
        private final Parameter.Value<GameMode> gameMode;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player, Parameter.Value<GameMode> gameMode) {
            this.player = player;
            this.gameMode = gameMode;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.player);
            GameMode gamemode = context.one(this.gameMode).orElseGet(() -> {
                GameMode currentGamemode = player.spongePlayer().get(Keys.GAME_MODE).orElseThrow(() -> new RuntimeException("Unknown gamemode"));
                return (currentGamemode.equals(GameModes.CREATIVE.get()) ? GameModes.SURVIVAL : GameModes.CREATIVE).get();
            });

            return GamemodeCommand.execute(player, gamemode);
        }
    }

    private GamemodeCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createGamemodeCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters
                .onlinePlayer(p -> true)
                .key("player")
                .requiredPermission(SPermissions.GAMEMODE_OTHER.node())
                .optional()
                .build();
        Parameter.Value<GameMode> gamemodeParameter = Parameter
                .registryElement(TypeToken.get(GameMode.class), RegistryTypes.GAME_MODE, ResourceKey.MINECRAFT_NAMESPACE)
                .key("gamemode")
                .optional()
                .build();

        return Command
                .builder()
                .addParameter(playerParameter)
                .addParameter(gamemodeParameter)
                .executor(new Execute(playerParameter, gamemodeParameter))
                .build();
    }

    public static CommandResult execute(@NotNull SGeneralPlayerData player, @NotNull GameMode modeToBe) {
        Player spongePlayer = player.spongePlayer();
        if (spongePlayer instanceof ServerPlayer) {
            Subject serverPlayer = (Subject) spongePlayer;
            SPermissions permission = SPermissions.getPermissionForGamemode(modeToBe);
            if (!serverPlayer.hasPermission(permission.node())) {
                return CommandResult.error(Component.text("You do not have the permission of " + permission.node()));
            }
        }
        spongePlayer.offer(Keys.GAME_MODE, modeToBe);
        return CommandResult.success();
    }


}
