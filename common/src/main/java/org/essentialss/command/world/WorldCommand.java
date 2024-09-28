package org.essentialss.command.world;

import org.spongepowered.api.command.Command;

import java.lang.reflect.InvocationTargetException;

public final class WorldCommand {

    private WorldCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createWorldCommand() {
        Command.Parameterized createNewWorldCommand;
        try {
            Class<?> createWorldCommandClass = Class.forName("org.essentialss.command.world.CreateWorldCommand");
            createNewWorldCommand = (Command.Parameterized) createWorldCommandClass.getMethod("createNewWorldCommand").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Is the sponge api module added?", e);
        }

        return Command
                .builder()
                .addChild(LoadWorldCommand.createLoadWorldCommand(), "load")
                .addChild(UnloadWorldCommand.createUnloadWorldCommand(), "unload")
                .addChild(createNewWorldCommand, "create")
                .build();
    }

}
