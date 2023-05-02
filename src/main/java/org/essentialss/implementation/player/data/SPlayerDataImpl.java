package org.essentialss.implementation.player.data;

import net.kyori.adventure.bossbar.BossBar;
import org.essentialss.api.message.MessageData;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.data.module.ModuleData;
import org.essentialss.api.player.teleport.PlayerTeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.arrays.OrderedUnmodifiableCollection;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.player.teleport.PlayerTeleportRequestImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SPlayerDataImpl extends AbstractProfileData implements SGeneralPlayerData {

    private final @NotNull Player player;
    private final Collection<TeleportRequest> teleportRequests = new LinkedHashSet<>();
    private final @NotNull Collection<SPlayerModifier<?>> afkModifiers = new LinkedHashSet<>();
    private @Nullable BossBar afkBar;
    private @Nullable Integer backTeleportIndex;
    private boolean isAfk;
    private LocalDateTime lastAction = LocalDateTime.now();

    public SPlayerDataImpl(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public PlayerTeleportRequest acceptTeleportRequest(@NotNull UUID other) throws IllegalStateException {
        Optional<PlayerTeleportRequest> opRequest = this.playerTeleportRequest(other);
        if (!opRequest.isPresent()) {
            throw new IllegalStateException("No request to accept");
        }
        PlayerTeleportRequest request = opRequest.get();
        this.teleportRequests.remove(request);
        Location<?, ?> location = request.teleportToLocation().apply(this);
        SGeneralPlayerData teleporter = null;
        switch (request.directionOfTeleport()) {
            case TOWARDS_REQUEST_HOLDER:
                teleporter = this;
                break;
            case TOWARDS_REQUEST_SENDER:
                SGeneralUnloadedData playerData = EssentialsSMain
                        .plugin()
                        .playerManager()
                        .get()
                        .dataFor(other)
                        .orElseThrow(() -> new IllegalStateException("UUID does not match any player"));
                if (!(playerData instanceof SGeneralPlayerData)) {
                    throw new IllegalStateException("Player is not online");
                }
                teleporter = (SGeneralPlayerData) playerData;
                break;
        }
        teleporter.teleport(location);
        return request;
    }

    @Override
    public Collection<SPlayerModifier<?>> appliedAwayFromKeyboardModifiers() {
        return this.afkModifiers;
    }

    @Override
    public @NotNull OptionalInt backTeleportIndex() {
        OrderedUnmodifiableCollection<OfflineLocation> locations = this.backTeleportLocations();
        if (locations.isEmpty()) {
            return OptionalInt.empty();
        }
        if (null == this.backTeleportIndex) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.backTeleportIndex);
    }

    @Override
    public Optional<BossBar> barUntilKick() {
        return Optional.ofNullable(this.afkBar);
    }

    @Override
    public PlayerTeleportRequest declineTeleportRequest(@NotNull UUID other) {
        Optional<PlayerTeleportRequest> opRequest = this.playerTeleportRequest(other);
        if (!opRequest.isPresent()) {
            throw new IllegalStateException("No request to decline");
        }
        this.teleportRequests.remove(opRequest.get());
        return opRequest.get();
    }

    @Override
    public boolean isShowingAwayFromKeyboard() {
        return this.isAfk;
    }

    @Override
    public LocalDateTime lastPlayerAction() {
        return this.lastAction;
    }

    @Override
    public void playerAction() {
        this.lastAction = LocalDateTime.now();
        this.isAfk = false;
        if (null != this.afkBar) {
            this.spongePlayer().hideBossBar(this.afkBar);
        }
    }

    @Override
    public @NotNull TeleportRequest register(@NotNull TeleportRequestBuilder builder) {
        TeleportRequest teleportRequest = new PlayerTeleportRequestImpl(builder);
        this.teleportRequests.add(teleportRequest);
        return teleportRequest;
    }

    @Override
    public void registerData(@NotNull ModuleData<?> moduleData) {
        this.moduleData.add(moduleData);
    }

    @Override
    public void releaseFromJail() {
        this.releaseFromJail(this.world().spawnPoint(this.player.position()).location());
    }

    @Override
    public void sendMessageTo(@NotNull MessageData data) {
        //noinspection allow-raw-message
        this.player.sendMessage(data.formattedMessage());
    }

    @Override
    public void setAwayFromKeyboardSince(@Nullable LocalDateTime since, @NotNull Collection<SPlayerModifier<?>> modifiers) {
        if (null != since) {
            this.lastAction = since;
        }
        this.isAfk = true;
        this.afkModifiers.clear();
        this.afkModifiers.addAll(modifiers);
    }

    @Override
    public void setBackTeleportIndex(int index) {
        OrderedUnmodifiableCollection<OfflineLocation> locations = this.backTeleportLocations();
        if (locations.isEmpty() || (locations.size() <= index)) {
            throw new IndexOutOfBoundsException("Back teleport locations is " + locations.size());
        }
        this.backTeleportIndex = index;
    }

    @Override
    public void setBarUntilKick(@Nullable BossBar bar) {
        this.afkBar = bar;
    }

    @Override
    public void setNextToKeyboard() {
        this.isAfk = false;
        this.afkModifiers.clear();
    }

    @Override
    public @NotNull Player spongePlayer() {
        return this.player;
    }

    @Override
    public @NotNull UnmodifiableCollection<TeleportRequest> teleportRequests() {
        this.updateTeleportRequests();
        return new SingleUnmodifiableCollection<>(this.teleportRequests);
    }

    @Override
    public @NotNull SWorldData world() {
        return EssentialsSMain.plugin().worldManager().get().dataFor(this.player.world());
    }

    @Override
    public void applyChangesFrom(@NotNull AbstractProfileData data) {
        super.applyChangesFrom(data);
        if (data instanceof SPlayerDataImpl) {
            SPlayerDataImpl pData = (SPlayerDataImpl) data;
            this.isAfk = pData.isAfk;
            this.teleportRequests.addAll(pData.teleportRequests);
            this.backTeleportIndex = pData.backTeleportIndex;
        }
    }

    @Override
    public @NotNull String playerName() {
        return this.player.name();
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

    @SuppressWarnings("DuplicateThrows")
    @Override
    public void reloadFromConfig() throws ConfigurateException, SerializationException {
        UserDataSerializer.load(this);
    }

    @SuppressWarnings("DuplicateThrows")
    @Override
    public void saveToConfig() throws ConfigurateException, SerializationException {
        UserDataSerializer.save(this);
    }

    private void updateTeleportRequests() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<TeleportRequest> toRemove = this.teleportRequests
                .parallelStream()
                .filter(r -> r.expiresAt().isPresent())
                .filter(r -> currentTime.isAfter(r.expiresAt().orElseThrow(() -> new RuntimeException("Broken logic"))))
                .collect(Collectors.toList());
        this.teleportRequests.removeAll(toRemove);
    }
}
