package org.essentialss.implementation.misc;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class CommandHelper {

    private CommandHelper() {
        throw new RuntimeException("Should not create");
    }

    public static Location<?, ?> locationOrTarget(CommandContext context, Parameter.Value<? extends Location<?, ?>> playerParameter) throws CommandException {
        return locationOrTarget(context, playerParameter, "Player must be specified");
    }

    public static Location<?, ?> locationOrTarget(CommandContext context, Parameter.Value<? extends Location<?, ?>> playerParameter, String noPlayer)
            throws CommandException {
        return locationOrTarget(context, playerParameter, Component.text(noPlayer));
    }

    public static Location<?, ?> locationOrTarget(CommandContext context, Parameter.Value<? extends Location<?, ?>> locationParameter, Component notLocatable)
            throws CommandException {
        return locationOrTarget(context, locationParameter, (l) -> l, notLocatable);
    }

    public static <T, S extends T> T locationOrTarget(CommandContext context,
                                                      Parameter.Value<S> locationParameter,
                                                      Function<Location<?, ?>, T> mapTo,
                                                      Component notLocatable) throws CommandException {
        Optional<S> opValue = context.one(locationParameter);
        if (opValue.isPresent()) {
            return opValue.get();
        }
        Optional<ServerLocation> opLocation = context.cause().location();
        if (opLocation.isPresent()) {
            return mapTo.apply(opLocation.get());
        }
        throw new CommandException(notLocatable);
    }

    public static <T extends SGeneralUnloadedData> T playerDataOrTarget(CommandContext context, Parameter.Value<T> playerParameter) throws CommandException {
        return playerDataOrTarget(context, playerParameter, "Player must be specified");
    }

    public static <T extends SGeneralUnloadedData> T playerDataOrTarget(CommandContext context, Parameter.Value<T> playerParameter, String noPlayer)
            throws CommandException {
        return playerDataOrTarget(context, playerParameter, Component.text(noPlayer));
    }

    public static <T extends SGeneralUnloadedData> T playerDataOrTarget(CommandContext context, Parameter.Value<T> playerParameter, Component noPlayer)
            throws CommandException {
        Optional<T> opPlayer = context.one(playerParameter);
        if (opPlayer.isPresent()) {
            return opPlayer.get();
        }
        return playerDataTarget(context, noPlayer);
    }

    public static <T extends SGeneralUnloadedData> T playerDataTarget(CommandContext context) throws CommandException {
        return playerDataTarget(context, "Player only command");
    }

    public static <T extends SGeneralUnloadedData> T playerDataTarget(CommandContext context, String noPlayer) throws CommandException {
        return playerDataTarget(context, Component.text(noPlayer));
    }

    public static <T extends SGeneralUnloadedData> T playerDataTarget(CommandContext context, Component noPlayer) throws CommandException {
        EventContext eventContext = context.cause().context();
        Optional<UUID> opTargetPlayerId = eventContext.get(EventContextKeys.CREATOR);
        if (!opTargetPlayerId.isPresent()) {
            throw new CommandException(noPlayer);
        }
        Optional<ServerPlayer> opTargetPlayer = Sponge.server().player(opTargetPlayerId.get());
        if (!opTargetPlayer.isPresent()) {
            throw new CommandException(noPlayer);
        }
        return (T) EssentialsSMain.plugin().playerManager().get().dataFor(opTargetPlayer.get());
    }

    public static <T extends Player> T playerOrTarget(@NotNull CommandContext context, Parameter.Value<T> parameter, Component noPlayer)
            throws CommandException {
        Optional<T> opPlayer = context.one(parameter);
        if (opPlayer.isPresent()) {
            return opPlayer.get();
        }

        Optional<UUID> opTargetPlayerId = context.cause().context().get(EventContextKeys.CREATOR);
        if (!opTargetPlayerId.isPresent()) {
            throw new CommandException(noPlayer);
        }
        Optional<ServerPlayer> opTargetPlayer = Sponge.server().player(opTargetPlayerId.get());
        if (opTargetPlayer.isPresent()) {
            return (T) opTargetPlayer.get();
        }
        throw new CommandException(noPlayer);

    }

    public static World<?, ?> worldOrTarget(CommandContext context, Parameter.Value<? extends World<?, ?>> worldParameter) throws CommandException {
        return worldOrTarget(context, worldParameter, "Player must be specified");
    }

    public static World<?, ?> worldOrTarget(CommandContext context, Parameter.Value<? extends World<?, ?>> worldParameter, String noPlayer)
            throws CommandException {
        return worldOrTarget(context, worldParameter, Component.text(noPlayer));
    }

    public static World<?, ?> worldOrTarget(CommandContext context, Parameter.Value<? extends World<?, ?>> worldParameter, Component notLocatable)
            throws CommandException {
        return locationOrTarget(context, worldParameter, Location::world, notLocatable);
    }

}
