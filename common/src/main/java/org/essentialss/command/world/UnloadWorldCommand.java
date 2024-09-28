package org.essentialss.command.world;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.arrays.OrderedUnmodifiableCollection;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.DefaultWorldKeys;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class UnloadWorldCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<ServerWorld> world;
        private final Parameter.Value<ServerWorld> offWorld;

        private Execute(Parameter.Value<ServerWorld> world, Parameter.Value<ServerWorld> offWorld) {
            this.world = world;
            this.offWorld = offWorld;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            ServerWorld world = context.requireOne(this.world);
            ServerWorld offWorld = context
                    .one(this.offWorld)
                    .orElseGet(
                            () -> Sponge.server().worldManager().world(DefaultWorldKeys.DEFAULT).orElseThrow(() -> new RuntimeException("No default world")));
            return UnloadWorldCommand.execute(context.cause().audience(), world, offWorld);
        }
    }

    private UnloadWorldCommand() {
        throw new RuntimeException("Should not generate");
    }

    static Command.Parameterized createUnloadWorldCommand() {
        Parameter.Value<ServerWorld> world = Parameter.world().key("world").build();
        Parameter.Value<ServerWorld> offDrop = Parameter.world().key("off").optional().build();
        return Command.builder().executor(new Execute(world, offDrop)).addParameter(world).addParameter(offDrop).build();
    }

    public static CommandResult execute(Audience audience, ServerWorld world, World<ServerWorld, ServerLocation> offDrop) {
        SWorldData offDropData = EssentialsSMain.plugin().worldManager().get().dataFor(offDrop);
        Map<SGeneralPlayerData, OfflineLocation> players = world
                .players()
                .stream()
                .map(p -> EssentialsSMain.plugin().playerManager().get().dataFor(p))
                .map(playerData -> {
                    OrderedUnmodifiableCollection<OfflineLocation> backLocations = playerData.backTeleportLocations();
                    for (int i = backLocations.size() - 1; 0 < i; i--) {
                        OfflineLocation loc = backLocations.get(i);
                        if (loc.world().isPresent() && !loc.world().get().equals(world)) {
                            return new AbstractMap.SimpleEntry<>(playerData, loc);
                        }
                    }
                    OfflineLocation loc = offDropData
                            .spawnPoints()
                            .stream()
                            .filter(sp -> sp.types().contains(SSpawnType.MAIN_SPAWN))
                            .findAny()
                            .map(SPoint::location)
                            .orElseThrow(() -> new RuntimeException("World doesn't have main spawn. This should be impossible"));
                    return new AbstractMap.SimpleEntry<>(playerData, loc);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        if (players.values().stream().anyMatch(offLoc -> offLoc.identifier().equalsIgnoreCase(world.key().formatted()))) {
            return CommandResult.error(Component.text("Invalid off world"));
        }

        players.forEach(SGeneralPlayerData::teleport);

        CompletableFuture<Boolean> future = Sponge.server().worldManager().unloadWorld(world);

        ResourceKey worldKey = world.key();
        Component unloadingMessage = EssentialsSMain.plugin().messageManager().get().adapters().unloadingWorld().get().adaptMessage(world);
        audience.sendMessage(unloadingMessage);

        future.thenAccept((success) -> {
            Component unloadedMessage = EssentialsSMain.plugin().messageManager().get().adapters().unloadWorld().get().adaptMessage(worldKey);
            audience.sendMessage(unloadedMessage);
        });
        return CommandResult.success();
    }

}
