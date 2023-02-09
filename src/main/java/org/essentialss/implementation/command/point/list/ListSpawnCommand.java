package org.essentialss.implementation.command.point.list;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.misc.CommandPager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListSpawnCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(@NotNull Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            int page = context.one(this.pageParameter).orElse(1);
            return ListSpawnCommand.execute(context.cause().audience(), page);
        }
    }

    private static Collection<? extends World<?, ?>> world() {
        if (Sponge.isServerAvailable()) {
            return Sponge.server().worldManager().worlds();
        }
        return Sponge.client().world().map(Collections::singletonList).orElse(Collections.emptyList());
    }

    public static CommandResult execute(@NotNull Audience audience, int page) {
        if (CommandPager.MINIMUM_PAGE > page) {
            page = CommandPager.MINIMUM_PAGE_SIZE;
        }
        SWorldManager worldManager = EssentialsSMain.plugin().worldManager().get();
        List<SSpawnPoint> spawns = world()
                .stream()
                .flatMap(world -> worldManager.dataFor(world).spawnPoints().stream())
                .sorted(Comparator.comparing(v -> v.position().toString()))
                .collect(Collectors.toList());

        CommandPager.displayList(audience, page, spawn -> Component.text(
                spawn.types().iterator().next().name() + " - " + spawn.position() + " - " + spawn
                        .worldData()
                        .identifier()), spawns);

        return CommandResult.success();
    }

    public static Command.Parameterized createSpawnListCommand() {
        Parameter.Value<Integer> pageNumberParameter = Parameter
                .rangedInteger(CommandPager.MINIMUM_PAGE, Integer.MAX_VALUE)
                .key("page")
                .optional()
                .build();
        return Command.builder().addParameter(pageNumberParameter).executor(new Execute(pageNumberParameter)).build();
    }

}
