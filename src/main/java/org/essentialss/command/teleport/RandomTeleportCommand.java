package org.essentialss.command.teleport;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.Constants;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.MatterType;
import org.spongepowered.api.data.type.MatterTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.border.WorldBorder;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector2d;

import java.util.Random;

public final class RandomTeleportCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> player;
        private final Parameter.Value<ServerWorld> world;

        private Execute(Parameter.Value<SGeneralPlayerData> player, Parameter.Value<ServerWorld> world) {
            this.player = player;
            this.world = world;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.player);
            World<?, ?> world = CommandHelper.worldOrTarget(context, this.world);
            if (!(world instanceof ServerWorld)) {
                throw new RuntimeException("Ran a server command on client");
            }
            executeAsync(player, (ServerWorld) world);
            context.cause().audience().sendMessage(Component.text("Finding safe location"));
            return CommandResult.success();
        }
    }

    private RandomTeleportCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createRandomTeleportCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters
                .onlinePlayer((p) -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.RANDOM_TELEPORT_OTHER.node())
                .build();
        Parameter.Value<ServerWorld> worldParameter = Parameter
                .world()
                .key("world")
                .optional()
                .requiredPermission(SPermissions.RANDOM_TELEPORT_WORLD.node())
                .build();
        return Command
                .builder()
                .executionRequirements((cause) -> Sponge.isServerAvailable())
                .executor(new Execute(playerParameter, worldParameter))
                .permission(SPermissions.RANDOM_TELEPORT_SELF.node())
                .build();
    }

    public static CommandResult execute(@NotNull SGeneralPlayerData player, @NotNull ServerWorld world) {
        WorldBorder border = world.border();
        double targetDim = border.targetDiameter();
        Vector2d center = border.center();
        Random random = world.random();
        int xLocation;
        int yLocation;
        int zLocation;
        while (true) {
            double x = random.nextInt((int) targetDim) / (double) Constants.TWO;
            double z = random.nextInt((int) targetDim) / (double) Constants.TWO;
            if (random.nextBoolean()) {
                x = -x;
            }
            if (random.nextBoolean()) {
                z = -z;
            }
            int xLoc = center.floorX() + (int) Math.round(x);
            int zLoc = center.floorY() + (int) Math.round(z);
            int maxHeight = world.maximumHeight();
            Integer safeHeight = null;
            for (int height = maxHeight; 0 < height; height--) {
                MatterType belowType = world
                        .location(xLoc, height, zLoc)
                        .get(Keys.MATTER_TYPE)
                        .orElseThrow(() -> new IllegalStateException("Block does not have matter type"));
                if (belowType.equals(MatterTypes.GAS.get())) {
                    continue;
                }
                if (belowType.equals(MatterTypes.LIQUID.get())) {
                    break;
                }
                MatterType standingType = world
                        .location(xLoc, height + 1, zLoc)
                        .get(Keys.MATTER_TYPE)
                        .orElseThrow(() -> new IllegalStateException("Block does not have matter type"));
                if (!standingType.equals(MatterTypes.GAS.get())) {
                    continue;
                }

                MatterType headType = world
                        .location(xLoc, height + 2, zLoc)
                        .get(Keys.MATTER_TYPE)
                        .orElseThrow(() -> new IllegalStateException("Block does not have matter type"));
                if (!headType.equals(MatterTypes.GAS.get())) {
                    continue;
                }
                safeHeight = height + 1;
                break;
            }
            if (null == safeHeight) {
                continue;
            }
            xLocation = xLoc;
            zLocation = zLoc;
            yLocation = safeHeight;
            break;
        }

        ServerLocation loc = world.location(xLocation, yLocation, zLocation);
        ServerLocation safeLocation = Sponge.server().teleportHelper().findSafeLocation(loc).orElse(loc);
        //ensures thread safe teleport
        Sponge.server().scheduler().executor(EssentialsSMain.plugin().container()).execute(() -> player.teleport(safeLocation));
        return CommandResult.success();
    }

    public static void executeAsync(@NotNull SGeneralPlayerData player, @NotNull ServerWorld world) {
        Sponge.asyncScheduler().executor(EssentialsSMain.plugin().container()).execute(() -> execute(player, world));
    }


}
