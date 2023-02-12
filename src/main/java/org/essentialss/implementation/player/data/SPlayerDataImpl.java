package org.essentialss.implementation.player.data;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.world.points.home.SHomeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public class SPlayerDataImpl extends AbstractUserData implements SGeneralPlayerData {

    private final @NotNull Player player;
    private boolean isAfk;
    private final Collection<TeleportRequest> teleportRequests = new LinkedHashSet<>();
    private int backTeleportIndex;
    private LinkedTransferQueue<SHome> homes = new LinkedTransferQueue<>();

    public SPlayerDataImpl(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.player.uniqueId();
    }

    @Override
    public void releaseFromJail(@NotNull OfflineLocation spawnTo) {
        this.isInJail = false;
        this.releaseFromJail = null;
        Optional<Location<?, ?>> opLoc = spawnTo.location();
        if (!opLoc.isPresent()) {
            throw new IllegalArgumentException("Cannot get location");
        }
        Location<?, ?> loc = opLoc.get();
        if (loc.onServer().isPresent()) {
            this.player.setLocation(loc.onServer().get());
        }
        if (!this.player.world().equals(loc.world())) {
            throw new IllegalStateException("World has not loaded. Cannot release from jail");
        }
        this.player.setPosition(spawnTo.position());
    }

    @Override
    public void releaseFromJail() {
        this.releaseFromJail(this.world().spawnPoint(this.player.position()).location());
    }

    @Override
    public void sendToJail(@NotNull SJailSpawnPoint point, @Nullable Duration length) {
        Location<?, ?> location = point.location().location().orElseThrow(() -> new IllegalStateException("World has not loaded"));
        if (location.onServer().isPresent()) {
            this.player.setLocation(location.onServer().get());
            this.isInJail = true;
            if (null != length) {
                this.releaseFromJail = LocalDateTime.now().plus(length);
            }
        }
        World<?, ?> world = location.world();
        if (!world.equals(this.player.world())) {
            throw new IllegalStateException("World has not loaded. Cannot send to jail");
        }
        this.player.setPosition(point.position());
        this.isInJail = true;
        if (null != length) {
            this.releaseFromJail = LocalDateTime.now().plus(length);
        }
    }

    @Override
    public void register(@NotNull SHomeBuilder builder) {
        SHome home = new SHomeImpl(builder);
        if (this.homes.parallelStream().anyMatch(h -> h.identifier().equalsIgnoreCase(builder.home()))) {
            throw new IllegalArgumentException("House already registered");
        }
        this.homes.add(home);
    }

    @Override
    public void deregister(@NotNull SHome home) {
        this.homes.remove(home);
    }

    @Override
    public @NotNull Player spongePlayer() {
        return this.player;
    }

    @Override
    public @NotNull SWorldData world() {
        return EssentialsSMain.plugin().worldManager().get().dataFor(this.player.world());
    }

    @Override
    public boolean isAwayFromKeyboard() {
        return this.isAfk;
    }

    @Override
    public void setAwayFromKeyboard(boolean afk) {
        this.isAfk = afk;
    }

    @Override
    public @NotNull UnmodifiableCollection<TeleportRequest> teleportRequests() {
        return new UnmodifiableCollection<>(this.teleportRequests);
    }

    @Override
    public @NotNull OptionalInt backTeleportIndex() {
        if (this.backTeleportLocations.isEmpty()) {
            return OptionalInt.empty();
        }
        if (this.backTeleportIndex >= this.backTeleportLocations.size()) {
            this.backTeleportIndex = this.backTeleportLocations.size() - 1;
        }
        return OptionalInt.of(this.backTeleportIndex);
    }

    @Override
    public void setBackTeleportIndex(int index) {
        if (this.backTeleportLocations.isEmpty() || (this.backTeleportLocations.size() <= index)) {
            throw new IndexOutOfBoundsException("Back teleport locations is " + this.backTeleportLocations.size());
        }
        this.backTeleportIndex = index;
    }

    @Override
    public void register(@NotNull TeleportRequestBuilder builder) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void decline(@NotNull TeleportRequest request) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void accept(@NotNull TeleportRequest request) throws IllegalStateException {
        throw new RuntimeException("Not implemented yet");

    }

    @Override
    public void reloadFromConfig() throws ConfigurateException {

    }

    @Override
    public void saveToConfig() throws ConfigurateException {
        UserDataSerializer.save(this);
    }
}
