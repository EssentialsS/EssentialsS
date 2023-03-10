package org.essentialss.implementation.command;

import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.function.Supplier;

public class CommandUtils {

    public static <T> T getTarget(@NotNull CommandContext context, Parameter.Value<T> parameter, Supplier<CommandException> supplier) throws CommandException {
        Optional<T> opPlayer = context.one(parameter);
        if (opPlayer.isPresent()) {
            return opPlayer.get();
        }
        if (context.subject() instanceof Player) {
            Player player = (Player) context.subject();
            return (T) EssentialsSMain.plugin().playerManager().get().dataFor(player);
        }
        throw supplier.get();

    }

}
