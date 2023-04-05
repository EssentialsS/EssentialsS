package org.essentialss.implementation.misc;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
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
        Subject subject = context.subject();
        if (!(subject instanceof Locatable)) {
            throw new CommandException(notLocatable);
        }
        Locatable locatable = (Locatable) subject;
        return mapTo.apply(locatable.location());
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
        Subject subject = context.subject();
        if (!(subject instanceof Player)) {
            throw new CommandException(noPlayer);
        }
        return (T) EssentialsSMain.plugin().playerManager().get().dataFor((Player) subject);
    }

    public static <T extends Player> T playerOrTarget(@NotNull CommandContext context, Parameter.Value<T> parameter, Component noPlayer)
            throws CommandException {
        Optional<T> opPlayer = context.one(parameter);
        if (opPlayer.isPresent()) {
            return opPlayer.get();
        }
        if (context.subject() instanceof Player) {
            return (T) context.subject();
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
