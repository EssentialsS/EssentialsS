package org.essentialss.implementation.command.point.list;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.misc.CommandPager;
import org.essentialss.implementation.permissions.permission.SPermissions;
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

public final class ListWarpCommand {

    private static final int MINIMUM_PAGE_SIZE = 1;

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(@NotNull Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            int page = context.one(this.pageParameter).orElse(1);
            return ListWarpCommand.execute(context.cause().audience(), page);
        }
    }

    private ListWarpCommand() {
        throw new RuntimeException("Should not create");
    }

    private static Collection<? extends World<?, ?>> world() {
        if (Sponge.isServerAvailable()) {
            return Sponge.server().worldManager().worlds();
        }
        return Sponge.client().world().map(Collections::singletonList).orElse(Collections.emptyList());
    }

    public static CommandResult execute(@NotNull Audience audience, int page) {
        if (MINIMUM_PAGE_SIZE > page) {
            page = MINIMUM_PAGE_SIZE;
        }
        SWorldManager worldManager = EssentialsSMain.plugin().worldManager().get();
        List<SWarp> warps = world()
                .stream()
                .flatMap(world -> worldManager.dataFor(world).warps().stream())
                .sorted(Comparator.comparing(StringIdentifier::identifier))
                .collect(Collectors.toList());

        CommandPager.displayList(audience, page, "Warps", "warps " + CommandPager.PAGE_ARGUMENT,
                                 warp -> Component.text(warp.identifier()), warps);
        return CommandResult.success();
    }

    public static Command.Parameterized createWarpListCommand() {
        Parameter.Value<Integer> pageNumberParameter = Parameter
                .rangedInteger(MINIMUM_PAGE_SIZE, Integer.MAX_VALUE)
                .key("page")
                .optional()
                .build();
        return Command
                .builder()
                .addParameter(pageNumberParameter)
                .executor(new Execute(pageNumberParameter))
                .permission(SPermissions.WARPS.node())
                .build();
    }

}
