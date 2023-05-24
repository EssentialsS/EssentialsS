package org.essentialss.command.world;

import org.spongepowered.api.command.Command;

public final class WorldCommand {

    private WorldCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createWorldCommand() {
        return Command
                .builder()
                .addChild(LoadWorldCommand.createLoadWorldCommand(), "load")
                .addChild(UnloadWorldCommand.createUnloadWorldCommand(), "unload")
                .addChild(CreateWorldCommand.createNewWorldCommand(), "create")
                .build();
    }

}
