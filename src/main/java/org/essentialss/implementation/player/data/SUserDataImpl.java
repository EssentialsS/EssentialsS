package org.essentialss.implementation.player.data;

import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SUserDataImpl extends AbstractUserData {

    private final @NotNull User user;

    public SUserDataImpl(@NotNull User user) {
        this.user = user;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.user.uniqueId();
    }

    @Override
    public void releaseFromJail(@NotNull Location<?, ?> spawnTo) {
        this.isInJail = false;
        this.releaseFromJail = null;
        World<?, ?> world = spawnTo.world();
        if (world instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld) world;
            this.user.setLocation(sWorld.key(), spawnTo.position());
            return;
        }
        throw new RuntimeException("Server must be active to modify offline players");
    }

    @Override
    public void releaseFromJail() {
        this.releaseFromJail(this.world().spawnPoint(this.user.position()).location());
    }

    @Override
    public void sendToJail(@NotNull SJailSpawnPoint point, @Nullable Duration length) {
        if (!(point.worldData().spongeWorld() instanceof ServerWorld)) {
            throw new IllegalStateException("Server must be active to send offline player to jail");
        }
        ServerWorld world = (ServerWorld) point.worldData().spongeWorld();
        this.user.setLocation(world.key(), point.position());
        this.isInJail = true;
        if (null != length) {
            this.releaseFromJail = LocalDateTime.now().plus(length);
        }
    }

    @Override
    public @NotNull SWorldData world() {
        ResourceKey key = this.user.worldKey();
        Optional<ServerWorld> opWorld = Sponge.server().worldManager().world(key);
        if (!opWorld.isPresent()) {
            throw new IllegalStateException("World of " + key.formatted() + " is not loaded");
        }
        return EssentialsSMain.plugin().worldManager().get().dataFor(opWorld.get());
    }
}
