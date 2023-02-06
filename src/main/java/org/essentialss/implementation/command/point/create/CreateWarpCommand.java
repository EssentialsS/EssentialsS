package org.essentialss.implementation.command.point.create;

import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
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

public class CreateWarpCommand {

    private static class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<Vector3d> locationParameter;
        private final @NotNull Parameter.Value<ServerWorld> worldParameter;
        private final @NotNull Parameter.Value<String> warpNameParameter;

        public Execute(@NotNull Parameter.Value<String> warpName,
                       @NotNull Parameter.Value<Vector3d> position,
                       @NotNull Parameter.Value<ServerWorld> world) {
            this.locationParameter = position;
            this.worldParameter = world;
            this.warpNameParameter = warpName;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerWorld> opWorld = context.one(this.worldParameter);
            World<?, ?> world;
            if (opWorld.isPresent()) {
                world = opWorld.get();
            } else if (Sponge.isClientAvailable()) {
                world = Sponge
                        .client()
                        .world()
                        .orElseThrow(() -> new CommandException(
                                Component.text("client is used but world could not be found")));
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            Optional<Vector3d> opPosition = context.one(this.locationParameter);
            Vector3d position;

            if (opPosition.isPresent()) {
                position = opPosition.get();
            } else if (context.subject() instanceof Locatable) {
                position = ((Locatable) context.subject()).location().position();
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            String warpName = context.requireOne(this.warpNameParameter);
            return CreateWarpCommand.execute(world.location(position), warpName, context.contextCause());
        }
    }

    public static CommandResult execute(@NotNull Location<?, ?> location,
                                        @NotNull String warpName,
                                        @NotNull Cause cause) {
        boolean added = EssentialsSMain
                .plugin()
                .worldManager()
                .get()
                .dataFor(location.world())
                .register(new SWarpBuilder().name(warpName).point(location.position()), cause);
        return added ? CommandResult.success() : CommandResult.error(Component.text("Could not register that warp"));
    }

    public static Command.Parameterized createRegisterWarpCommand() {
        Parameter.Key<String> warpNameKey = Parameter.key("name", String.class);
        Parameter.Key<Vector3d> locationKey = Parameter.key("location", Vector3d.class);
        Parameter.Key<ServerWorld> worldKey = Parameter.key("world", ServerWorld.class);


        Parameter.Value<Vector3d> location = SParameters.location(false).key(locationKey).optional().build();
        Parameter.Value<String> warpName = Parameter.string().key(warpNameKey).build();
        Parameter.Value<ServerWorld> world = Parameter
                .world()
                .key(worldKey)
                .optional()
                .requirements(cause -> Sponge.isServerAvailable())
                .build();

        return Command
                .builder()
                .addParameter(warpName)
                .addParameter(location)
                .addParameter(world)
                .executor(new Execute(warpName, location, world))
                .build();
    }

}
