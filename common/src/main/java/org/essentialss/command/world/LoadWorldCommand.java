package org.essentialss.command.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class LoadWorldCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<ResourceKey> worldKey;

        private Execute(Parameter.Value<ResourceKey> worldKey) {
            this.worldKey = worldKey;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            ResourceKey key = context.requireOne(this.worldKey);
            return LoadWorldCommand.execute(context.cause().audience(), key);
        }
    }

    private LoadWorldCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createLoadWorldCommand() {
        Parameter.Value<ResourceKey> worldKey = Parameter
                .resourceKey()
                .key("world")
                .completer((context, currentInput) -> Sponge
                        .server()
                        .worldManager()
                        .worldKeys()
                        .stream()
                        .map(ResourceKey::formatted)
                        .filter(key -> key.toLowerCase().startsWith(currentInput.toLowerCase()))
                        .map(CommandCompletion::of)
                        .collect(Collectors.toList()))
                .build();
        return Command.builder().executor(new Execute(worldKey)).addParameter(worldKey).build();
    }

    public static CommandResult execute(Audience audience, ResourceKey worldKey) {
        WorldManager worldManager = Sponge.server().worldManager();
        if (!worldManager.worldExists(worldKey)) {
            Component message = EssentialsSMain.plugin().messageManager().get().adapters().noWorldByThatKey().get().adaptMessage(worldKey);
            return CommandResult.error(message);
        }
        Optional<ServerWorld> opWorld = worldManager.worlds().stream().filter(world -> world.key().equals(worldKey)).findAny();
        if (opWorld.isPresent()) {
            Component message = EssentialsSMain.plugin().messageManager().get().adapters().worldHasAlreadyLoaded().get().adaptMessage(opWorld.get());
            return CommandResult.error(message);
        }

        CompletableFuture<ServerWorld> future = worldManager.loadWorld(worldKey);

        Component message = EssentialsSMain.plugin().messageManager().get().adapters().loadingWorld().get().adaptMessage(worldKey);
        audience.sendMessage(message);

        future.thenAccept((world) -> {
            Component loadedWorldMessage = EssentialsSMain.plugin().messageManager().get().adapters().loadedWorld().get().adaptMessage(world);
            audience.sendMessage(loadedWorldMessage);
        });
        return CommandResult.success();
    }

}
