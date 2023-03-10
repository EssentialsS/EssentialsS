package org.essentialss.implementation.command.essentialss;

import org.essentialss.implementation.command.essentialss.config.message.SetMessageCommand;
import org.essentialss.implementation.command.essentialss.config.message.ViewMessageCommand;
import org.spongepowered.api.command.Command;

public final class EssentialsSCommand {

    private EssentialsSCommand() {
        throw new RuntimeException("Cannot run");
    }

    private static Command.Parameterized createConfigCommand() {
        return Command.builder().addChild(createConfigMessageCommand(), "message").build();
    }

    private static Command.Parameterized createConfigMessageCommand() {
        return Command
                .builder()
                .addChild(SetMessageCommand.createSetMessageCommand(), "set")
                .addChild(ViewMessageCommand.createViewMessageCommand(), "view")
                .build();
    }

    public static Command.Parameterized createEssentialsCommand() {
        return Command.builder().addChild(createConfigCommand(), "config").build();
    }

}
