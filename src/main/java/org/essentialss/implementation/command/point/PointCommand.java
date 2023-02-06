package org.essentialss.implementation.command.point;

import org.essentialss.implementation.command.point.create.CreateWarpCommand;
import org.essentialss.implementation.command.point.teleport.TeleportToWarpCommand;
import org.spongepowered.api.command.Command;

public class PointCommand {

    public static Command.Parameterized createWarpCommand() {
        Command.Parameterized register = CreateWarpCommand.createRegisterWarpCommand();
        Command.Parameterized teleportTo = TeleportToWarpCommand.createWarpToCommand();

        return TeleportToWarpCommand.createWarpToCommand(
                Command.builder().addChild(register, "create", "register").addChild(teleportTo, "teleport", "to"));
    }

}
