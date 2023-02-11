package org.essentialss.implementation.command.point.create;

import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

public class CreateSpawnCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<ServerWorld> worldParameter;
        private final @NotNull Parameter.Value<Double> xParameter;
        private final @NotNull Parameter.Value<Double> yParameter;
        private final @NotNull Parameter.Value<Double> zParameter;

        private final @NotNull Parameter.Value<SSpawnType> spawnTypeParameter;

        public Execute(@NotNull Parameter.Value<SSpawnType> spawnTypeParameter,
                       @NotNull Parameter.Value<ServerWorld> world,
                       @NotNull Parameter.Value<Double> x,
                       @NotNull Parameter.Value<Double> y,
                       @NotNull Parameter.Value<Double> z) {
            this.spawnTypeParameter = spawnTypeParameter;
            this.worldParameter = world;
            this.xParameter = x;
            this.yParameter = y;
            this.zParameter = z;
        }


        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SSpawnType spawnType = context.requireOne(this.spawnTypeParameter);
            Optional<World<?, ?>> opWorld = context.one(this.worldParameter).map(world -> world);
            Optional<Double> opX = context.one(this.xParameter);
            Optional<Double> opY = context.one(this.yParameter);
            Optional<Double> opZ = context.one(this.zParameter);
            if (!opWorld.isPresent() && (context.subject() instanceof Locatable)) {
                opWorld = Optional.of(((Locatable) context.subject()).world());
            }
            if (!opX.isPresent() && (context.subject() instanceof Locatable)) {
                opX = Optional.of(((Locatable) context.subject()).location().x());
            }
            if (!opY.isPresent() && (context.subject() instanceof Locatable)) {
                opY = Optional.of(((Locatable) context.subject()).location().y());
            }
            if (!opZ.isPresent() && (context.subject() instanceof Locatable)) {
                opZ = Optional.of(((Locatable) context.subject()).location().z());
            }

            if (!opWorld.isPresent() || !opX.isPresent() || !opY.isPresent() || !opZ.isPresent()) {
                return CommandResult.error(Component.text("Location needs to be specified"));
            }

            SWorldData world = EssentialsSMain.plugin().worldManager().get().dataFor(opWorld.get());
            OfflineLocation loc = new OfflineLocation(world, new Vector3d(opX.get(), opY.get(), opZ.get()));
            return CreateSpawnCommand.execute(loc, spawnType, context.contextCause());
        }
    }

    public static CommandResult execute(@NotNull OfflineLocation location,
                                        @NotNull SSpawnType type,
                                        @NotNull Cause cause) {
        boolean result = location
                .worldData()
                .register(new SSpawnPointBuilder().setSpawnTypes(type).setPoint(location.position()), cause);
        return result ? CommandResult.success() : CommandResult.error(Component.text("Could not create spawn"));
    }

    public static Command.Parameterized createSpawnCommand() {
        Parameter.Value<SSpawnType> spawnType = SParameters.spawnType().key("type").build();
        Parameter.Value<Double> x = SParameters.location(false, Location::x).key("x").optional().build();
        Parameter.Value<Double> y = SParameters.location(false, Location::y).key("y").optional().build();
        Parameter.Value<Double> z = SParameters.location(false, Location::z).key("z").optional().build();
        Parameter.Value<ServerWorld> world = Parameter.world().key("world").optional().build();

        return Command
                .builder()
                .addParameter(spawnType)
                .addParameter(x)
                .addParameter(y)
                .addParameter(z)
                .addParameter(world)
                .executor(new Execute(spawnType, world, x, y, z))
                .build();

    }
}
