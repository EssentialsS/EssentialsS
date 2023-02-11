package org.essentialss.implementation.command.point;

import org.essentialss.implementation.command.point.create.CreateSpawnCommand;
import org.essentialss.implementation.command.point.create.CreateWarpCommand;
import org.essentialss.implementation.command.point.list.ListWarpCommand;
import org.essentialss.implementation.command.point.teleport.TeleportToSpawnCommand;
import org.essentialss.implementation.command.point.teleport.TeleportToWarpCommand;
import org.spongepowered.api.command.Command;

public class PointCommand {

    public static Command.Parameterized createWarpCommand() {
        Command.Parameterized register = CreateWarpCommand.createRegisterWarpCommand();
        Command.Parameterized teleportTo = TeleportToWarpCommand.createWarpToCommand();
        Command.Parameterized list = ListWarpCommand.createWarpListCommand();

        return TeleportToWarpCommand.createWarpToCommand(Command
                                                                 .builder()
                                                                 .addChild(register, "create", "register")
                                                                 .addChild(teleportTo, "teleport", "to")
                                                                 .addChild(list, "list", "display"));
    }

    public static Command.Parameterized createSpawnCommand() {
        Command.Parameterized create = CreateSpawnCommand.createSpawnCommand();


        return TeleportToSpawnCommand.createSpawnToCommand(Command.builder().addChild(create, "create", "new"));
    }

}
