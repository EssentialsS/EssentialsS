package org.essentialss.player.data;

import net.kyori.adventure.bossbar.BossBar;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.message.MessageData;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.BackToKeyboardMessageAdapter;
import org.essentialss.api.modifier.SPlayerModifier;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.teleport.PlayerTeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.OrderedUnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleOrderedUnmodifiableCollection;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.events.player.afk.PlayerAwayFromKeyboardImpl;
import org.essentialss.player.teleport.PlayerTeleportRequestImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;
import org.mose.property.impl.nevernull.ReadOnlyNeverNullPropertyImpl;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public class SPlayerDataImpl extends AbstractProfileData implements SGeneralPlayerData {

    private final Player player;
    private final Property.Write<BossBar, BossBar> barUntilKick = new WritePropertyImpl<>(t -> t, null);
    private final CollectionProperty.Write<TeleportRequest, OrderedUnmodifiableCollection<TeleportRequest>> teleportRequests;
    private final CollectionProperty.Write<SPlayerModifier<?>, Collection<SPlayerModifier<?>>> afkModifier;
    private final Property.Write<Integer, Integer> backTeleportIndex;
    private final Property.Write<LocalDateTime, LocalDateTime> lastAction;
    private final Property.Write<LocalDateTime, LocalDateTime> kitCooldownRelease;
    private final ReadOnlyNeverNullPropertyImpl<Boolean, Boolean> isAfk;
    private final WriteNeverNullPropertyImpl<Boolean, Boolean> shownAfk;
    private final WritePropertyImpl<UUID, UUID> viewingInventoryOf;

    public SPlayerDataImpl(Player player) {
        this.player = player;

        this.viewingInventoryOf = new WritePropertyImpl<>(t -> t, null);
        this.afkModifier = new WriteCollectionPropertyImpl<>(t -> t, LinkedTransferQueue::new);
        this.backTeleportIndex = new WritePropertyImpl<>(t -> t, null);
        this.shownAfk = WriteNeverNullPropertyImpl.bool();
        this.lastAction = new WritePropertyImpl<>(t -> t, LocalDateTime.now());
        this.kitCooldownRelease = new WritePropertyImpl<>(t -> t, null);
        this.isAfk = new ReadOnlyNeverNullPropertyImpl<>(t -> t, () -> false, null);
        //noinspection unchecked
        this.teleportRequests = new WriteCollectionPropertyImpl<>(t -> {
            List<TeleportRequest> requests = new LinkedList<>(t);
            requests.sort(Comparator.comparing(req -> req.expiresAt().orElse(LocalDateTime.MAX)));
            return new SingleOrderedUnmodifiableCollection<>(requests);
        }, SingleOrderedUnmodifiableCollection::new, new LinkedTransferQueue<>());
        this.isAfk.bindTo(this.lastAction, localDateTime -> {
            AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
            Duration duration;
            try {
                duration = config.durationUntilStatus().parse(config);
            } catch (SerializationException e) {
                return false;
            }
            if (null == duration) {
                return false;
            }
            LocalDateTime willBeAfk = localDateTime.plus(duration);
            return LocalDateTime.now().isAfter(willBeAfk);
        });
        this.isAfk.registerValueChangeEvent((property, oldValue, valueSetType, newValue) -> {
            if (newValue) {
                return;
            }
            SPlayerDataImpl.this.shownAfk.setValue(false);
        });
        this.shownAfk.registerValueChangeEvent((property, oldValue, valueSetType, newValue) -> {
            if (!Sponge.isServerAvailable()) {
                return;
            }
            if (!newValue) {
                BackToKeyboardMessageAdapter messageAdapter = EssentialsSMain.plugin().messageManager().get().adapters().backToKeyboard().get();
                if (messageAdapter.isEnabled()) {
                    Sponge.server().broadcastAudience().sendMessage(messageAdapter.adaptMessage(SPlayerDataImpl.this));
                }
                return;
            }
            Singleton<AwayFromKeyboardMessageAdapter> messageAdapter = EssentialsSMain.plugin().messageManager().get().adapters().awayFromKeyboard();
            Cause cause = Cause.of(EventContext
                                           .builder()
                                           .add(EventContextKeys.PLUGIN, EssentialsSMain.plugin().container())
                                           .add(EventContextKeys.PLAYER, SPlayerDataImpl.this.spongePlayer())
                                           .build(), SPlayerDataImpl.this);
            Cancellable event = new PlayerAwayFromKeyboardImpl(SPlayerDataImpl.this, cause);
            if (event.isCancelled()) {
                return;
            }

            if (Sponge.isServerAvailable()) {
                Sponge.server().broadcastAudience().sendMessage(messageAdapter.get().adaptMessage(SPlayerDataImpl.this));
            }
        });
    }

    @Override
    public PlayerTeleportRequest acceptTeleportRequest(@NotNull UUID playerId) throws IllegalStateException {
        Optional<PlayerTeleportRequest> opRequest = this.playerTeleportRequest(playerId);
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
                        .dataFor(playerId)
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
    public CollectionProperty.Write<SPlayerModifier<?>, Collection<SPlayerModifier<?>>> awayFromKeyboardModifierProperties() {
        return this.afkModifier;
    }

    @Override
    public Property.Write<Integer, Integer> backTeleportIndexProperty() {
        return this.backTeleportIndex;
    }

    @Override
    public Property.ReadOnly<BossBar, BossBar> barUntilKickedProperty() {
        return this.getReadOnly(this.barUntilKick);
    }

    @Override
    public PlayerTeleportRequest declineTeleportRequest(@NotNull UUID playerId) throws IllegalStateException {
        Optional<PlayerTeleportRequest> opRequest = this.playerTeleportRequest(playerId);
        if (!opRequest.isPresent()) {
            throw new IllegalStateException("No request to decline");
        }
        this.teleportRequests.remove(opRequest.get());
        return opRequest.get();
    }

    @Override
    public <P extends Property.Write<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P hasShownAwayFromKeyboardMessageProperty() {
        return (P) this.shownAfk;
    }

    @Override
    public <P extends Property.ReadOnly<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P isShowingAwayFromKeyboardProperty() {
        return (P) this.isAfk;
    }

    @Override
    public Property.Write<LocalDateTime, LocalDateTime> kitCooldownReleaseProperty() {
        return this.kitCooldownRelease;
    }

    @Override
    public Property.Write<LocalDateTime, LocalDateTime> lastActionProperty() {
        return this.lastAction;
    }

    @NotNull
    @Override
    public TeleportRequest register(@NotNull TeleportRequestBuilder builder) {
        TeleportRequest teleportRequest = new PlayerTeleportRequestImpl(builder);
        this.teleportRequests.add(teleportRequest);
        return teleportRequest;
    }

    @Override
    public void sendMessageTo(@NotNull MessageData data) {
        this.player.sendMessage(data.formattedMessage());
    }

    @Override
    public void setAwayFromKeyboardSince(@Nullable LocalDateTime since, Collection<SPlayerModifier<?>> modifiers) {
        if (null != since) {
            this.lastAction.setValue(since);
        }
        boolean isAfk = this.isAfk.value().orElse(false);
        this.shownAfk.setValue(isAfk);
        this.afkModifier.setValue(modifiers);
    }

    @Override
    public void setBarUntilKick(@Nullable BossBar bar) {
        this.barUntilKick.setValue(bar);
    }

    @Override
    public void setNextToKeyboard() {
        this.shownAfk.setValue(false);
        this.afkModifier.setValue(Collections.emptyList());
    }

    @NotNull
    @Override
    public Player spongePlayer() {
        return this.player;
    }

    @Override
    public CollectionProperty.ReadOnly<TeleportRequest, OrderedUnmodifiableCollection<TeleportRequest>> teleportRequestsProperty() {
        this.updateTeleportRequests();
        return this.getReadOnly(this.teleportRequests);
    }

    @Override
    public Property.Write<UUID, UUID> viewingInventoryOfProperty() {
        return this.viewingInventoryOf;
    }

    @NotNull
    @Override
    public SWorldData world() {
        return EssentialsSMain.plugin().worldManager().get().dataFor(this.player.world());
    }

    @Override
    public void applyChangesFrom(@NotNull AbstractProfileData data) {
        super.applyChangesFrom(data);
        if (data instanceof SPlayerDataImpl) {
            SPlayerDataImpl pData = (SPlayerDataImpl) data;
            this.shownAfk.setValue(pData.shownAfk.value().orElse(false));
            //noinspection unchecked
            this.teleportRequests.addAll(pData.teleportRequests.value().orElseGet(SingleOrderedUnmodifiableCollection::new));
            this.backTeleportIndex.setValue(pData.backTeleportIndex.value().orElse(0));
        }
    }

    @NotNull
    @Override
    public String playerName() {
        return this.player.name();
    }

    @Override
    public void releaseFromJail(@NotNull OfflineLocation location) {
        this.isInJail.setValue(false);
        this.releasedFromJail.setValue(null);
        Optional<Location<?, ?>> opLoc = location.location();
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
        this.player.setPosition(location.position());
    }

    @Override
    public void sendToJail(@NotNull SJailSpawnPoint point, @Nullable Duration length) {
        Location<?, ?> location = point.location().location().orElseThrow(() -> new IllegalStateException("World has not loaded"));
        if (location.onServer().isPresent()) {
            this.player.setLocation(location.onServer().get());
            this.isInJail.setValue(true);
            if (null != length) {
                this.releasedFromJail.setValue(LocalDateTime.now().plus(length));
            }
        }
        World<?, ?> world = location.world();
        if (!world.equals(this.player.world())) {
            throw new IllegalStateException("World has not loaded. Cannot send to jail");
        }
        this.player.setPosition(point.position());
        this.isInJail.setValue(true);
        if (null != length) {
            this.releasedFromJail.setValue(LocalDateTime.now().plus(length));
        }
    }

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
                .value()
                .orElseGet(() -> new SingleOrderedUnmodifiableCollection<>(Collections.emptyList()))
                .parallelStream()
                .filter(r -> r.expiresAt().isPresent())
                .filter(r -> currentTime.isAfter(r.expiresAt().orElseThrow(() -> new RuntimeException("Broken logic"))))
                .collect(Collectors.toList());
        this.teleportRequests.removeAll(toRemove);
    }
}
