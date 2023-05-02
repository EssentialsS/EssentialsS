package org.essentialss.implementation.command.essentialss.plugins;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.implementation.misc.CommandPager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.plugin.PluginContainer;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;

public class PluginsCommand {

    private static class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            int page = context.one(this.pageParameter).orElse(1);
            return PluginsCommand.execute(context.cause().audience(), page);
        }
    }

    public static Command.Parameterized createPluginsCommand() {
        Parameter.Value<Integer> pageParameter = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();
        return Command.builder().executor(new Execute(pageParameter)).build();
    }

    public static CommandResult execute(@NotNull Audience audience, int page) {
        Collection<PluginContainer> plugins = Sponge.pluginManager().plugins();
        CommandPager.displayList(audience, page, "plugins", "essentialss plugins", pluginContainer -> {
            Component pluginName = Component.text(pluginContainer.metadata().name().orElse(pluginContainer.metadata().id())).color(NamedTextColor.AQUA);
            Component pluginVersion = Component.text(pluginContainer.metadata().version().toString()).color(NamedTextColor.GREEN);

            Component toSend = pluginName.append(Component.text(" - ")).append(pluginVersion);

            Optional<URL> opHomepage = pluginContainer.metadata().links().homepage();
            if (opHomepage.isPresent()) {
                TextComponent homePageComponent = Component.text("Homepage").clickEvent(ClickEvent.openUrl(opHomepage.get())).color(NamedTextColor.GOLD);
                return toSend.append(Component.text(" - ").append(homePageComponent));
            }
            return toSend;
        }, plugins);
        return CommandResult.success();
    }

}
