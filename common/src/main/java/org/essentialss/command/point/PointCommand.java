package org.essentialss.command.point;

import org.essentialss.command.point.create.CreateHomeCommand;
import org.essentialss.command.point.create.CreateSpawnCommand;
import org.essentialss.command.point.create.CreateWarpCommand;
import org.essentialss.command.point.delete.DeleteHomeCommand;
import org.essentialss.command.point.delete.DeleteWarpCommand;
import org.essentialss.command.point.list.ListHomeCommand;
import org.essentialss.command.point.list.ListWarpCommand;
import org.essentialss.command.point.teleport.TeleportToHomeCommand;
import org.essentialss.command.point.teleport.TeleportToSpawnCommand;
import org.essentialss.command.point.teleport.TeleportToWarpCommand;
import org.spongepowered.api.command.Command;

public final class PointCommand {

    private PointCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createSpawnCommand() {
        Command.Parameterized create = CreateSpawnCommand.createSpawnCommand();


        return TeleportToSpawnCommand.createSpawnToCommand(Command.builder().addChild(create, "create", "register", "new"));
    }

    public static Command.Parameterized createHomeCommand() {
        Command.Parameterized register = CreateHomeCommand.createRegisterHomeCommand();
        Command.Parameterized delete = DeleteHomeCommand.createDeleteHomeCommand();
        Command.Parameterized teleport = TeleportToHomeCommand.createHomeToCommand();
        Command.Parameterized list = ListHomeCommand.createHomeListCommand();

        return TeleportToHomeCommand.createHomeToCommand(Command
                                                                 .builder()
                                                                 .addChild(register, "create", "register", "new")
                                                                 .addChild(delete, "delete", "remove")
                                                                 .addChild(teleport, "teleport", "tp", "to")
                                                                 .addChild(list, "list", "display"));
    }

    public static Command.Parameterized createWarpCommand() {
        Command.Parameterized register = CreateWarpCommand.createRegisterWarpCommand();
        Command.Parameterized teleportTo = TeleportToWarpCommand.createWarpToCommand();
        Command.Parameterized list = ListWarpCommand.createWarpListCommand();
        Command.Parameterized delete = DeleteWarpCommand.createDeleteWarpCommand();

        return TeleportToWarpCommand.createWarpToCommand(Command
                                                                 .builder()
                                                                 .addChild(delete, "delete", "remove")
                                                                 .addChild(register, "create", "register", "new")
                                                                 .addChild(teleportTo, "teleport", "tp", "to")
                                                                 .addChild(list, "list", "display"));
    }

}
