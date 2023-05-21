package org.essentialss.player.data;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
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

    private final User user;

    public SUserDataImpl(User user) {
        this.user = user;
    }

    @NotNull
    @Override
    public String playerName() {
        return this.user.name();
    }

    @NotNull
    @Override
    public UUID uuid() {
        return this.user.uniqueId();
    }

    @Override
    public void releaseFromJail(@NotNull OfflineLocation location) {
        this.isInJail.setValue(false);
        this.releasedFromJail.setValue(null);
        Location<?, ?> spawnTo = location.location().orElseThrow(() -> new RuntimeException("Server must be active to modify offline players"));
        Optional<ServerLocation> serverLocation = spawnTo.onServer();
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
        this.isInJail.setValue(true);
        if (null != length) {
            this.releasedFromJail.setValue(LocalDateTime.now().plus(length));
        }
    }

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
