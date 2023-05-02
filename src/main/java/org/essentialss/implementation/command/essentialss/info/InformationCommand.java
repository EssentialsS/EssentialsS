package org.essentialss.implementation.command.essentialss.info;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.plugin.PluginContainer;

public class InformationCommand {

    private static class Execute implements CommandExecutor {

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            return InformationCommand.execute(context.cause().audience());
        }
    }

    public static Command.Parameterized createInfoCommand() {
        return Command.builder().executor(new Execute()).build();
    }

    public static CommandResult execute(@NotNull Audience audience) {
        audience.sendMessage(format(EssentialsSMain.plugin().container().metadata().name().orElse("Unknown") + " version",
                                    EssentialsSMain.plugin().container().metadata().version().toString()));
        audience.sendMessage(format("O.S", System.getProperty("os.name")));
        audience.sendMessage(format("Java Implementation", System.getProperty("java.runtime.name")));
        audience.sendMessage(format("Java Version", System.getProperty("java.version")));
        audience.sendMessage(format("Minecraft Version", Sponge.platform().minecraftVersion().name()));
        audience.sendMessage(format("Sponge API", Sponge.platform().container(Platform.Component.API).metadata().version().toString()));
        PluginContainer implementation = Sponge.platform().container(Platform.Component.IMPLEMENTATION);
        audience.sendMessage(format(implementation.metadata().name().orElse(implementation.metadata().id()), implementation.metadata().version().toString()));

        return CommandResult.success();
    }

    private static Component format(String title, String value, TextColor colourOverride) {
        TextComponent componentTitle = Component.text(title + ": ").color(NamedTextColor.AQUA);
        TextComponent componentValue = Component.text(value).color(colourOverride);
        return componentTitle.append(componentValue);
    }

    private static Component format(String title, String value) {
        return format(title, value, NamedTextColor.GOLD);
    }
}
