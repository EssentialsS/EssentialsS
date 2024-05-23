package org.essentialss.command.point.create;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Optional;

public final class CreateHomeCommand {

    private static final class Execute implements CommandExecutor {

        private final @NotNull Parameter.Value<Double> locationXParameter;
        private final @NotNull Parameter.Value<Double> locationYParameter;
        private final @NotNull Parameter.Value<Double> locationZParameter;

        private final @NotNull Parameter.Value<ServerWorld> worldParameter;
        private final @NotNull Parameter.Value<String> homeNameParameter;
        private final @NotNull Parameter.Value<SGeneralUnloadedData> playerDataParameter;

        private Execute(@NotNull Parameter.Value<SGeneralUnloadedData> playerData,
                        @NotNull Parameter.Value<String> homeName,
                        @NotNull Parameter.Value<Double> x,
                        @NotNull Parameter.Value<Double> y,
                        @NotNull Parameter.Value<Double> z,
                        @NotNull Parameter.Value<ServerWorld> world) {
            this.locationXParameter = x;
            this.locationYParameter = y;
            this.locationZParameter = z;
            this.worldParameter = world;
            this.homeNameParameter = homeName;
            this.playerDataParameter = playerData;
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

            Optional<SGeneralUnloadedData> opPlayerData = context.one(this.playerDataParameter);
            SGeneralUnloadedData playerData;
            if (opPlayerData.isPresent()) {
                playerData = opPlayerData.get();
            } else if (context.subject() instanceof Player) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                throw new CommandException(EssentialsSMain.plugin().messageManager().get().adapters().playerOnlyCommand().get().adaptMessage());
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

            String homeName = context.requireOne(this.homeNameParameter);
            CommandResult result = CreateHomeCommand.execute(context.cause().audience(), playerData, world.location(posX, posY, posZ), homeName,
                                                             context.contextCause());
            if (!result.isSuccess()) {
                context.cause().audience().sendMessage(Component.text("Could not register new warp"));
            }
            return result;
        }
    }

    private CreateHomeCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createRegisterHomeCommand() {
        Parameter.Key<SGeneralUnloadedData> userKey = Parameter.key("user", SGeneralUnloadedData.class);
        Parameter.Key<String> warpNameKey = Parameter.key("name", String.class);
        Parameter.Key<Double> xKey = Parameter.key("x", Double.class);
        Parameter.Key<Double> yKey = Parameter.key("y", Double.class);
        Parameter.Key<Double> zKey = Parameter.key("z", Double.class);
        Parameter.Key<ServerWorld> worldKey = Parameter.key("world", ServerWorld.class);

        Parameter.Value<SGeneralUnloadedData> user = SParameters.offlinePlayersNickname(false, general -> {
            if (!(general instanceof SGeneralPlayerData)) {
                return true;
            }
            SGeneralPlayerData playerData = (SGeneralPlayerData) general;
            Player player = playerData.spongePlayer();
            if (player instanceof Subject) {
                return ((Subject) player).hasPermission(SPermissions.HOME_TELEPORT_SELF.node());
            }
            return true;
        }).requiredPermission(SPermissions.HOME_CREATE_OTHER.node()).key(userKey).optional().build();
        Parameter.Value<Double> x = SParameters.location(false, Location::x).key(xKey).optional().build();
        Parameter.Value<Double> y = SParameters.location(false, Location::y).key(yKey).optional().build();
        Parameter.Value<Double> z = SParameters.location(false, Location::z).key(zKey).optional().build();

        Parameter.Value<String> homeName = Parameter.string().key(warpNameKey).build();
        Parameter.Value<ServerWorld> world = Parameter.world().key(worldKey).optional().requirements(cause -> Sponge.isServerAvailable()).build();

        return Command
                .builder()
                .addParameter(user)
                .addParameter(homeName)
                .addParameter(x)
                .addParameter(y)
                .addParameter(z)
                .addParameter(world)
                .permission(SPermissions.HOME_CREATE_SELF.node())
                .executor(new Execute(user, homeName, x, y, z, world))
                .build();
    }

    public static CommandResult execute(@NotNull Audience audience,
                                        @NotNull SGeneralUnloadedData playerData,
                                        @NotNull Location<?, ?> location,
                                        @NotNull String homeName,
                                        @NotNull Cause cause) {
        Optional<SHome> added;
        try {
            added = playerData.register(new SHomeBuilder().setHome(homeName).setPoint(location), cause);
        } catch (IllegalArgumentException e) {
            return CommandResult.error(Component.text(e.getMessage()));
        }
        if (!added.isPresent()) {
            return CommandResult.error(Component.text("Could not register that home"));
        }
        try {
            playerData.saveToConfig();
        } catch (ConfigurateException e) {
            return CommandResult.error(Component.text((null == e.getMessage()) ? e.getLocalizedMessage() : e.getMessage()));
        }

        Component component = EssentialsSMain.plugin().messageManager().get().adapters().createHome().get().adaptMessage(added.get());
        audience.sendMessage(component);
        return CommandResult.success();
    }

}
