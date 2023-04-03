package org.essentialss.implementation.command.point.create;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.warp.SWarp;
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
import org.spongepowered.configurate.ConfigurateException;

import java.util.Optional;

public final class CreateWarpCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<Double> locationXParameter;
        private final @NotNull Parameter.Value<Double> locationYParameter;
        private final @NotNull Parameter.Value<Double> locationZParameter;

        private final @NotNull Parameter.Value<ServerWorld> worldParameter;
        private final @NotNull Parameter.Value<String> warpNameParameter;

        private Execute(@NotNull Parameter.Value<String> warpName,
                        @NotNull Parameter.Value<Double> x,
                        @NotNull Parameter.Value<Double> y,
                        @NotNull Parameter.Value<Double> z,
                        @NotNull Parameter.Value<ServerWorld> world) {
            this.locationXParameter = x;
            this.locationYParameter = y;
            this.locationZParameter = z;
            this.worldParameter = world;
            this.warpNameParameter = warpName;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<ServerWorld> opWorld = context.one(this.worldParameter);
            World<?, ?> world;
            if (opWorld.isPresent()) {
                world = opWorld.get();
            } else if (context.subject() instanceof Locatable) {
                world = ((Locatable) context.subject()).world();
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            Optional<Double> opPositionX = context.one(this.locationXParameter);
            Optional<Double> opPositionY = context.one(this.locationYParameter);
            Optional<Double> opPositionZ = context.one(this.locationZParameter);

            double posX;
            double posY;
            double posZ;

            if (opPositionX.isPresent()) {
                posX = opPositionX.get();
            } else if (context.subject() instanceof Locatable) {
                posX = ((Locatable) context.subject()).location().position().x();
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            if (opPositionY.isPresent()) {
                posY = opPositionY.get();
            } else if (context.subject() instanceof Locatable) {
                posY = ((Locatable) context.subject()).location().position().y();
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            if (opPositionZ.isPresent()) {
                posZ = opPositionZ.get();
            } else if (context.subject() instanceof Locatable) {
                posZ = ((Locatable) context.subject()).location().position().z();
            } else {
                throw new CommandException(Component.text("Location must be specified"));
            }

            String warpName = context.requireOne(this.warpNameParameter);
            CommandResult result = CreateWarpCommand.execute(context.cause().audience(), world.location(posX, posY, posZ), warpName, context.contextCause());
            if (!result.isSuccess()) {
                context.cause().audience().sendMessage(Component.text("Could not register new warp"));
            }
            return result;
        }
    }

    private CreateWarpCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createRegisterWarpCommand() {
        Parameter.Key<String> warpNameKey = Parameter.key("name", String.class);
        Parameter.Key<Double> xKey = Parameter.key("x", Double.class);
        Parameter.Key<Double> yKey = Parameter.key("y", Double.class);
        Parameter.Key<Double> zKey = Parameter.key("z", Double.class);
        Parameter.Key<ServerWorld> worldKey = Parameter.key("world", ServerWorld.class);

        Parameter.Value<Double> x = SParameters.location(false, Location::x).key(xKey).optional().build();
        Parameter.Value<Double> y = SParameters.location(false, Location::y).key(yKey).optional().build();
        Parameter.Value<Double> z = SParameters.location(false, Location::z).key(zKey).optional().build();

        Parameter.Value<String> warpName = Parameter.string().key(warpNameKey).build();
        Parameter.Value<ServerWorld> world = Parameter.world().key(worldKey).optional().requirements(cause -> Sponge.isServerAvailable()).build();

        return Command
                .builder()
                .addParameter(warpName)
                .addParameter(x)
                .addParameter(y)
                .addParameter(z)
                .addParameter(world)
                .executor(new Execute(warpName, x, y, z, world))
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull Location<?, ?> location, @NotNull String warpName, @NotNull Cause cause) {

        SWorldData worldData = EssentialsSMain.plugin().worldManager().get().dataFor(location.world());
        try {
            //Ensures any user manually added values are loaded
            worldData.reloadFromConfig();
        } catch (ConfigurateException e) {
            return CommandResult.error(Component.text((null == e.getMessage()) ? e.getLocalizedMessage() : e.getMessage()));
        }
        Optional<SWarp> added;
        try {
            added = worldData.register(new SWarpBuilder().setName(warpName).setPoint(location.position()), cause);
        } catch (IllegalArgumentException e) {
            return CommandResult.error(Component.text(e.getMessage()));
        }
        if (!added.isPresent()) {
            return CommandResult.error(Component.text("Could not register that warp"));
        }
        try {
            worldData.saveToConfig();
        } catch (ConfigurateException e) {
            return CommandResult.error(Component.text((null == e.getMessage()) ? e.getLocalizedMessage() : e.getMessage()));
        }

        Component component = EssentialsSMain.plugin().messageManager().get().adapters().createWarp().get().adaptMessage(added.get());
        audience.sendMessage(component);
        return CommandResult.success();
    }
}

