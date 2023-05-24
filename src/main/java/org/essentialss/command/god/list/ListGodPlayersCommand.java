package org.essentialss.command.god.list;

import net.kyori.adventure.audience.Audience;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.compare.PlayerCompare;
import org.essentialss.misc.CommandPager;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.List;
import java.util.stream.Collectors;

public final class ListGodPlayersCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            int page = context.one(this.pageParameter).orElse(1);
            return ListGodPlayersCommand.execute(context.cause().audience(), page);
        }
    }

    private ListGodPlayersCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createListGodPlayersCommand() {
        Parameter.Value<Integer> pageParameter = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();
        return Command.builder().addParameter(pageParameter).executor(new Execute(pageParameter)).build();
    }

    public static CommandResult execute(Audience audience, int page) {
        List<SGeneralUnloadedData> players = EssentialsSMain
                .plugin()
                .playerManager()
                .get()
                .unloadedDataForAll()
                .stream()
                .filter(SGeneralUnloadedData::hasGodMode)
                .sorted(PlayerCompare.isOnline().thenComparing(PlayerCompare.displayName()))
                .collect(Collectors.toList());

        CommandPager.displayList(audience, page, "Players in GodMode", "god list", SGeneralUnloadedData::displayName, players);
        return CommandResult.success();
    }

}
