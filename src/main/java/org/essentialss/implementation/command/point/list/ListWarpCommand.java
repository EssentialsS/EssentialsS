package org.essentialss.implementation.command.point.list;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.api.config.GeneralConfig;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.world.SWorldManager;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.implementation.EssentialsSMain;
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

public class ListWarpCommand {

    private static final int MINIMUM_PAGE_SIZE = 1;

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(@NotNull Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) {
            int page = context.one(this.pageParameter).orElse(0);
            return ListWarpCommand.execute(context.cause().audience(), page);
        }
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
        GeneralConfig config = EssentialsSMain.plugin().configManager().get().general().get();
        int pageSize = config.pageSize().parseDefault(config);
        if (MINIMUM_PAGE_SIZE > pageSize) {
            pageSize = MINIMUM_PAGE_SIZE;
        }
        int indexMax = pageSize * page;
        int indexMin = indexMax - pageSize;
        int totalPages = (warps.size() / pageSize);

        if (indexMin >= warps.size()) {
            CommandResult.error(Component.text("Page cannot be found. Maximum page is " + totalPages));
        }
        indexMax = Math.min(indexMax, warps.size());
        List<SWarp> toDisplay = warps.subList(indexMin, indexMax);
        audience.sendMessage(Component
                                     .text("====[")
                                     .color(NamedTextColor.AQUA)
                                     .append(Component.text("Warps (page " + page + "/" + totalPages))
                                     .append(Component.text("]===").color(NamedTextColor.AQUA)));
        toDisplay.forEach(warp -> audience.sendMessage(Component.text(warp.identifier())));
        Component pageNext = Component.empty();
        boolean hasPrevious = false;

        if (MINIMUM_PAGE_SIZE != page) {
            Component component = Component
                    .text("<<Previous<<")
                    .clickEvent(ClickEvent.runCommand("warps list " + (page - 1)));
            pageNext = pageNext.append(component);
            hasPrevious = true;
        }
        if (page != totalPages) {
            if (hasPrevious) {
                Component splitComponent = Component.text(" | ");
                pageNext = pageNext.append(splitComponent);
            }
            Component nextPage = Component.text(">>Next>>").clickEvent(ClickEvent.runCommand("warps list " + page + 1));
            pageNext = pageNext.append(nextPage);
        }
        if (!pageNext.equals(Component.empty())) {
            audience.sendMessage(pageNext);
        }
        return CommandResult.success();
    }

    public static Command.Parameterized createWarpListCommand() {
        Parameter.Value<Integer> pageNumberParameter = Parameter
                .rangedInteger(MINIMUM_PAGE_SIZE, Integer.MAX_VALUE)
                .key("page")
                .optional()
                .build();
        return Command.builder().addParameter(pageNumberParameter).executor(new Execute(pageNumberParameter)).build();
    }

}
