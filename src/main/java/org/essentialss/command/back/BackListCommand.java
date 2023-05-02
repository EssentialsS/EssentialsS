package org.essentialss.command.back;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.misc.CommandHelper;
import org.essentialss.misc.CommandPager;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;

public class BackListCommand {

    private static class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        public Execute(Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataTarget(context);
            int page = context.one(this.pageParameter).orElse(1);
            return BackListCommand.execute(player, page);
        }
    }

    public static Command.Parameterized createBackListCommand() {
        Parameter.Value<Integer> pageParameter = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();

        return Command.builder().addParameter(pageParameter).executor(new Execute(pageParameter)).build();
    }

    public static CommandResult execute(SGeneralPlayerData player, int page) {
        if (page <= 0) {
            return CommandResult.error(Component.text("Unknown page number"));
        }
        List<OfflineLocation> backTeleportLocations = player.backTeleportLocations();
        OptionalInt opIndex = player.backTeleportIndex();
        if (opIndex.isPresent()) {
            backTeleportLocations = backTeleportLocations.subList(0, opIndex.getAsInt());
        }
        List<OfflineLocation> finalList = new LinkedList<>(backTeleportLocations);
        Collections.reverse(finalList);
        CommandPager.displayList(player.spongePlayer(), page, "Back locations", "backl", offlineLocation -> {
            Component world = Component.text(offlineLocation.identifier()).color(NamedTextColor.GOLD);
            Component x = Component.text(offlineLocation.position().x()).color(NamedTextColor.RED);
            Component y = Component.text(offlineLocation.position().y()).color(NamedTextColor.GREEN);
            Component z = Component.text(offlineLocation.position().z()).color(NamedTextColor.BLUE);

            return world.append(Component.text(", ")).append(x).append(Component.text(", ")).append(y).append(Component.text(", ")).append(z);
        }, finalList);
        return CommandResult.success();
    }

}
