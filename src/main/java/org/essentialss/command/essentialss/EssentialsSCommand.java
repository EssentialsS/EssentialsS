package org.essentialss.command.essentialss;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.SConfig;
import org.essentialss.api.config.SConfigManager;
import org.essentialss.command.essentialss.performance.PerformanceCommand;
import org.essentialss.command.essentialss.config.SetConfigCommand;
import org.essentialss.command.essentialss.config.ViewConfigCommand;
import org.essentialss.command.essentialss.config.message.SetMessageCommand;
import org.essentialss.command.essentialss.config.message.ViewMessageCommand;
import org.essentialss.command.essentialss.info.InformationCommand;
import org.essentialss.command.essentialss.plugins.PluginsCommand;
import org.essentialss.command.essentialss.update.CheckForUpdateCommand;
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
        return Command
                .builder()
                .addChild(createUpdateCommand(), "update")
                .addChild(createConfigCommand(), "config")
                .addChild(PluginsCommand.createPluginsCommand(), "plugins", "pl")
                .addChild(PerformanceCommand.createPerformanceCommand(), "performance")
                .addChild(InformationCommand.createInfoCommand(), "info")
                .build();
    }

    private static Command.Parameterized createUpdateCommand() {
        return Command.builder().addChild(CheckForUpdateCommand.createUpdateCheckCommand(), "check").build();
    }

}
