package org.essentialss.implementation.command.back;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.misc.CommandHelper;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class BackCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> playerParameter;
        private final Parameter.Value<Integer> spacesParameter;
        private final Parameter.Value<ServerWorld> worldParameter;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> playerParameter,
                        Parameter.Value<Integer> spacesParameter,
                        Parameter.Value<ServerWorld> worldParameter) {
            this.playerParameter = playerParameter;
            this.spacesParameter = spacesParameter;
            this.worldParameter = worldParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            int spaces = context.one(this.spacesParameter).orElse(1);
            return BackCommand.execute(player, spaces,
                                       context.one(this.worldParameter).map(w -> EssentialsSMain.plugin().worldManager().get().dataFor(w)).orElse(null));
        }
    }

    public static Command.Parameterized createBackCommand() {
        Parameter.Value<Integer> spacesParameter = Parameter
                .rangedInteger(1, Integer.MAX_VALUE)
                .key("spaces")
                .optional()
                .requiredPermission(SPermissions.BACK_SPACES.node())
                .build();
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters
                .onlinePlayer(player -> !player.backTeleportLocations().isEmpty())
                .key("player")
                .requiredPermission(SPermissions.BACK_OTHER.node())
                .optional()
                .build();
        Parameter.Value<ServerWorld> worldParameter = Parameter.world().key("world").optional().build();

        return Command
                .builder()
                .executor(new Execute(playerParameter, spacesParameter, worldParameter))
                .addParameter(spacesParameter)
                .addParameter(worldParameter)
                .addParameter(playerParameter)
                .build();
    }

    public static CommandResult execute(SGeneralPlayerData player, int spaces, @Nullable SWorldData targetWorld) {
        if (spaces <= 0) {
            return CommandResult.error(Component.text("Back spaces must be positive"));
        }
        List<OfflineLocation> backTeleportLocations = player.backTeleportLocations();
        List<OfflineLocation> currentLocations = backTeleportLocations;
        if (null != targetWorld) {
            currentLocations = currentLocations.stream().filter(world -> world.identifier().equals(targetWorld.identifier())).collect(Collectors.toList());
        }
        OptionalInt opIndex = player.backTeleportIndex();
        if (opIndex.isPresent()) {
            currentLocations = currentLocations.subList(0, opIndex.getAsInt());
        }

        if (currentLocations.size() < spaces) {
            return CommandResult.error(Component.text("Cannot go back that far"));
        }
        OfflineLocation loc = currentLocations.get(currentLocations.size() - spaces);

        int masterIndex = backTeleportLocations.indexOf(loc);
        player.setBackTeleportIndex(masterIndex);
        player.teleport(loc);
        return CommandResult.success();
    }
}
