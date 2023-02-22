package org.essentialss.implementation.player.data;

import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SUserDataImpl extends AbstractProfileData implements SGeneralOfflineData {

    private final @NotNull User user;


    public SUserDataImpl(@NotNull User user) {
        this.user = user;
    }

    @Override
    public String playerName() {
        return this.user.name();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.user.uniqueId();
    }

    @Override
    public void releaseFromJail(@NotNull OfflineLocation spawnTo) {
        this.isInJail = false;
        this.releaseFromJail = null;
        Location<?, ?> location = spawnTo.location().orElseThrow(() -> new RuntimeException("Server must be active to modify offline players"));
        Optional<ServerLocation> serverLocation = location.onServer();
        if (serverLocation.isPresent()) {
            this.user.setLocation(serverLocation.get().worldKey(), serverLocation.get().position());
            return;
        }
        throw new RuntimeException("Server must be active to modify offline players");
    }

    @Override
    public void releaseFromJail() {
        Optional<SWorldData> opWorld = this.world();
        if (!opWorld.isPresent()) {
            throw new RuntimeException("Could not get world: " + this.user.worldKey().formatted());
        }
        this.releaseFromJail(opWorld.get().spawnPoint(this.user.position()).location());
    }

    @Override
    public void sendToJail(@NotNull SJailSpawnPoint point, @Nullable Duration length) {
        Optional<ResourceKey> opWorldKey = point.worldData().worldKey();
        if (!opWorldKey.isPresent()) {
            throw new IllegalStateException("Cannot get world key for " + point.worldData().identifier());
        }
        this.user.setLocation(opWorldKey.get(), point.position());
        this.isInJail = true;
        if (null != length) {
            this.releaseFromJail = LocalDateTime.now().plus(length);
        }
    }

    @SuppressWarnings("DuplicateThrows")
    @Override
    public void reloadFromConfig() throws ConfigurateException, SerializationException {
        UserDataSerializer.load(this);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public void saveToConfig() throws ConfigurateException {
        UserDataSerializer.save(this);
    }

    public @NotNull Optional<SWorldData> world() {
        ResourceKey key = this.user.worldKey();
        return Sponge.server().worldManager().world(key).map(world -> EssentialsSMain.plugin().worldManager().get().dataFor(world));
    }
}
