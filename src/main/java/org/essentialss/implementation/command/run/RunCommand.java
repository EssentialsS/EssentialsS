package org.essentialss.implementation.command.run;

import org.spongepowered.api.command.Command;

public class RunCommand {

    private static Command.Parameterized createRunCMDCommand() {
        Command.Parameterized consoleCommand = RunConsoleCMDCommand.createRunConsoleCommand();
        Command.Parameterized playerCommand = RunPlayerCMDCommand.createRunPlayerCommand();

        return Command
                .builder()
                .addChild(consoleCommand, "console", "terminal", "cmd")
                .addChild(playerCommand, "player")
                .build();
    }

    public static Command.Parameterized createRunCommand() {
        Command.Parameterized cmdCommand = createRunCMDCommand();
        return Command.builder().addChild(cmdCommand, "command", "cmd").build();
    }

}
