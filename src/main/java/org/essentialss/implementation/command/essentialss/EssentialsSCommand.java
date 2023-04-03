package org.essentialss.implementation.command.essentialss;

import org.essentialss.api.config.SConfig;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.command.essentialss.config.SetConfigCommand;
import org.essentialss.implementation.command.essentialss.config.ViewConfigCommand;
import org.essentialss.implementation.command.essentialss.config.message.SetMessageCommand;
import org.essentialss.implementation.command.essentialss.config.message.ViewMessageCommand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;

import java.util.stream.Collectors;

public final class EssentialsSCommand {

    private EssentialsSCommand() {
        throw new RuntimeException("Cannot run");
    }

    private static Command.Parameterized createConfigCommand(SConfig config) {
        return Command
                .builder()
                .addChild(SetConfigCommand.createSetConfigCommand(config), "set")
                .addChild(ViewConfigCommand.createViewConfigCommand(config), "view")
                .build();
    }

    private static Command.Parameterized createConfigCommand() {
        @NotNull SConfigManager configManager = EssentialsSMain.plugin().configManager().get();
        return Command
                .builder()
                .addChild(createConfigCommand(configManager.awayFromKeyboard().get()), "afk")
                .addChild(createConfigCommand(configManager.ban().get()), "ban")
                .addChild(createConfigCommand(configManager.general().get()), "general")
                .addChild(createConfigMessageCommand(), "message")
                .build();
    }

    private static Command.Parameterized createConfigMessageCommand() {
        return Command
                .builder()
                .addChild(SetMessageCommand.createSetMessageCommand(), "set")
                .addChild(ViewMessageCommand.createViewMessageCommand(
                        () -> EssentialsSMain.plugin().messageManager().get().adapters().all().collect(Collectors.toList())), "view")
                .build();
    }

    public static Command.Parameterized createEssentialsCommand() {
        return Command.builder().addChild(createConfigCommand(), "config").build();
    }

}
