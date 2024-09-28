package org.essentialss.command.point.list;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.utils.identifier.StringIdentifier;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.misc.CommandPager;
import org.essentialss.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class ListHomeCommand {

    private static final int MINIMUM_PAGE_SIZE = 1;

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;
        private final Parameter.Value<SGeneralUnloadedData> playerDataParameter;

        private Execute(@NotNull Parameter.Value<SGeneralUnloadedData> playerDataParameter, @NotNull Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
            this.playerDataParameter = playerDataParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Optional<SGeneralUnloadedData> opPlayerData = context.one(this.playerDataParameter);
            SGeneralUnloadedData playerData;
            if (opPlayerData.isPresent()) {
                playerData = opPlayerData.get();
            } else if (context.subject() instanceof Player) {
                playerData = EssentialsSMain.plugin().playerManager().get().dataFor((Player) context.subject());
            } else {
                throw new CommandException(EssentialsSMain.plugin().messageManager().get().adapters().playerOnlyCommand().get().adaptMessage());
            }

            int page = context.one(this.pageParameter).orElse(1);
            return ListHomeCommand.execute(context.cause().audience(), playerData, page);
        }
    }

    private ListHomeCommand() {
        throw new RuntimeException("Should not create");
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull SGeneralUnloadedData playerData, int page) {
        if (MINIMUM_PAGE_SIZE > page) {
            page = MINIMUM_PAGE_SIZE;
        }
        List<SHome> homes = playerData.homes().stream().sorted(Comparator.comparing(StringIdentifier::identifier)).collect(Collectors.toList());

        CommandPager.displayList(audience, page, "Homes", "homes " + CommandPager.PAGE_ARGUMENT, warp -> Component.text(warp.identifier()), homes);
        return CommandResult.success();
    }

    public static Command.Parameterized createHomeListCommand() {
        Parameter.Value<SGeneralUnloadedData> playerDataParameter = SParameters
                .offlinePlayersNickname(false, general -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.HOMES_OTHER.node())
                .build();
        Parameter.Value<Integer> pageNumberParameter = Parameter.rangedInteger(MINIMUM_PAGE_SIZE, Integer.MAX_VALUE).key("page").optional().build();
        return Command
                .builder()
                .addParameter(pageNumberParameter)
                .executor(new Execute(playerDataParameter, pageNumberParameter))
                .permission(SPermissions.HOMES_SELF.node())
                .build();
    }

}
