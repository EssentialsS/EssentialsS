package org.essentialss.implementation.command.point.list;

import net.kyori.adventure.audience.Audience;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListWarpCommand {

    public class Execute implements CommandExecutor {

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {

        }
    }

    private static Collection<? extends World<?, ?>> world() {
        if (Sponge.isServerAvailable()) {
            return Sponge.server().worldManager().worlds();
        }
        return Sponge.client().world().map(Collections::singletonList).orElse(Collections.emptyList());
    }

    public static CommandResult execute(@NotNull Audience audience, int page) {
        SWorldManager worldManager = EssentialsSMain.plugin().worldManager().get();
        List<SWarp> warps = world()
                .stream()
                .flatMap(world -> worldManager.dataFor(world).warps().stream())
                .sorted(Comparator.comparing(StringIdentifier::identifier))
                .collect(Collectors.toList());

        return CommandResult.success();
    }

}
