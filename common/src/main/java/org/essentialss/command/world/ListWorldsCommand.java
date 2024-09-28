package org.essentialss.command.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.world.SWorldData;
import org.essentialss.misc.CommandPager;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ListWorldsCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> page;

        private Execute(Parameter.Value<Integer> page) {
            this.page = page;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            int page = context.one(this.page).orElse(1);
            return ListWorldsCommand.execute(context.cause().audience(), page);
        }
    }

    private ListWorldsCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createListCommand() {
        Parameter.Value<Integer> page = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();
        return Command.builder().addParameter(page).executor(new Execute(page)).build();
    }

    public static CommandResult execute(Audience audience, int page) {
        List<SWorldData> worldData = EssentialsSMain
                .plugin()
                .worldManager()
                .get()
                .allWorldData()
                .stream()
                .sorted(Comparator.comparing(StringIdentifier::identifier))
                .collect(Collectors.toList());
        CommandPager.displayList(audience, page, "worlds", "worlds", (data) -> {
            TextComponent worldName = Component.text(data.identifier());
            Component isLoaded = Component.text(" - Loaded: " + data.isLoadedProperty().value().orElse(false));
            return worldName.append(isLoaded);
        }, worldData);
        return CommandResult.success();
    }

}
