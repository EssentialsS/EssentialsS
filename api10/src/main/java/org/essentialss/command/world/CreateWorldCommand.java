package org.essentialss.command.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.essentialss.EssentialsSMain;
import org.essentialss.misc.StringHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldTemplate;

import java.util.concurrent.CompletableFuture;

public final class CreateWorldCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<ServerWorld> basedFrom;
        private final Parameter.Value<Component> displayName;

        private Execute(Parameter.Value<ServerWorld> basedFrom, Parameter.Value<Component> displayName) {
            this.basedFrom = basedFrom;
            this.displayName = displayName;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            ServerWorld basedOn = context.requireOne(this.basedFrom);
            Component name = context.requireOne(this.displayName);
            return CreateWorldCommand.execute(context.cause().audience(), basedOn, name);
        }
    }

    private CreateWorldCommand() {
        throw new RuntimeException("Should not generate");
    }

    static Command.Parameterized createNewWorldCommand() {
        Parameter.Value<ServerWorld> basedOn = Parameter.world().key("basedOn").build();
        Parameter.Value<Component> name = Parameter.formattingCodeTextOfRemainingElements().key("name").build();

        return Command.builder().executor(new Execute(basedOn, name)).addParameter(basedOn).addParameter(name).build();
    }

    public static CommandResult execute(Audience audience, ServerWorld world, Component displayName) {
        String idName = StringHelper.toIdFormat(PlainTextComponentSerializer.plainText().serialize(displayName));
        ResourceKey id = ResourceKey.of(EssentialsSMain.plugin().container(), idName);
        @NotNull WorldTemplate template = WorldTemplate.builder().from(world).add(Keys.DISPLAY_NAME, displayName).key(id).build();
        CompletableFuture<ServerWorld> future = Sponge.server().worldManager().loadWorld(template);

        Component creatingMessage = EssentialsSMain.plugin().messageManager().get().adapters().creatingWorld().get().adaptMessage(id);

        audience.sendMessage(creatingMessage);
        future.thenAccept((loaded) -> {
            Component loadedMessage = EssentialsSMain.plugin().messageManager().get().adapters().createdWorld().get().adaptMessage(loaded);
            audience.sendMessage(loadedMessage);
        });
        return CommandResult.success();
    }

}
