package org.essentialss.command.god;

import org.essentialss.command.god.list.ListDemiGodPlayersCommand;
import org.essentialss.command.god.list.ListGodPlayersCommand;
import org.essentialss.command.god.set.SetDemiGodModeCommand;
import org.essentialss.command.god.set.SetGodModeCommand;
import org.spongepowered.api.command.Command;

public final class GodModeCommand {
    private GodModeCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createDemiGodModeCommand() {
        return Command
                .builder()
                .addChild(SetDemiGodModeCommand.createEnableDemiGodModeCommand(), "enable")
                .addChild(SetDemiGodModeCommand.createDisableDemiGodModeCommand(), "disable")
                .addChild(ListDemiGodPlayersCommand.createListDemiGodPlayersCommand(), "list")
                .build();
    }

    public static Command.Parameterized createGodModeCommand() {
        return Command
                .builder()
                .addChild(SetGodModeCommand.createEnableGodModeCommand(), "enable")
                .addChild(SetGodModeCommand.createDisableGodModeCommand(), "disable")
                .addChild(ListGodPlayersCommand.createListGodPlayersCommand(), "list")
                .build();
    }

}
