package org.essentialss.implementation.player.data;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.data.module.ModuleData;
import org.essentialss.api.player.data.module.SerializableModuleData;
import org.essentialss.api.player.mail.MailMessage;
import org.essentialss.api.player.mail.MailMessageBuilder;
import org.essentialss.api.utils.arrays.SingleUnmodifiableCollection;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.implementation.world.points.home.SHomeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;

public abstract class AbstractProfileData implements SGeneralUnloadedData {

    final @NotNull LinkedList<OfflineLocation> backTeleportLocations = new LinkedList<>();
    final @NotNull LinkedTransferQueue<ModuleData<?>> moduleData = new LinkedTransferQueue<>();
    private final @NotNull LinkedList<MailMessage> mailMessages = new LinkedList<>();
    private final @NotNull LinkedTransferQueue<SHome> homes = new LinkedTransferQueue<>();
    boolean isInJail;
    @Nullable LocalDateTime releaseFromJail;
    private boolean canLooseItemsWhenUsed;
    @Nullable
    private Component displayName;
    private boolean preventingTeleportRequests;

    @Override
    public void addBackTeleportLocation(@NotNull OfflineLocation location) {
        this.backTeleportLocations.add(location);
    }

    @Override
    public void addMailMessage(@NotNull MailMessageBuilder builder) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public @NotNull LinkedList<OfflineLocation> backTeleportLocations() {
        return this.backTeleportLocations;
    }

    @Override
    public boolean canLooseItemsWhenUsed() {
        return this.canLooseItemsWhenUsed;
    }

    @Override
    public void deregister(@NotNull SHome home) {
        this.homes.remove(home);
    }

    @Override
    public void deregisterData(@NotNull ResourceKey key) {
        this.moduleData.parallelStream().filter(d -> d.key().equals(key)).forEach(this.moduleData::remove);
    }

    @Override
    public @NotNull Component displayName() {
        if (null == this.displayName) {
            return Component.text(this.playerName());
        }
        return this.displayName;

    }

    @Override
    public <T extends ModuleData<?>> Optional<T> getData(@NotNull ResourceKey key) {
        return this.moduleData.parallelStream().filter(moduleData -> moduleData.key().equals(key)).findAny().map(data -> (T) data);
    }

    @Override
    public boolean hasSetDisplayName() {
        return null != this.displayName;
    }

    @Override
    public @NotNull UnmodifiableCollection<SHome> homes() {
        throw new RuntimeException("homes not implemented yet");
    }

    @Override
    public boolean isInJail() {
        return this.isInJail;
    }

    @Override
    public boolean isPreventingTeleportRequests() {
        return this.preventingTeleportRequests;
    }

    @Override
    public @NotNull UnmodifiableCollection<MailMessage> mailMessages() {
        return new SingleUnmodifiableCollection<>(this.mailMessages);
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
    public void registerOfflineData(@NotNull SerializableModuleData<?> moduleData) {
        this.deregisterData(moduleData);
        this.moduleData.add(moduleData);
    }

    @Override
    public Optional<LocalDateTime> releasedFromJailTime() {
        return Optional.ofNullable(this.releaseFromJail);
    }

    @Override
    public void removeBackTeleportLocation(@NotNull OfflineLocation location) {
        this.backTeleportLocations.remove(location);
    }

    @Override
    public void removeMessage(@NotNull MailMessage message) {
        this.mailMessages.remove(message);
    }

    @Override
    public void setBackTeleportLocations(Collection<OfflineLocation> locations) {
        this.backTeleportLocations.clear();
        this.backTeleportLocations.addAll(locations);
    }

    @Override
    public void setCanLooseItemsWhenUsed(boolean check) {
        this.canLooseItemsWhenUsed = check;
    }

    @Override
    public void setDisplayName(@Nullable Component component) {
        this.displayName = component;
    }

    @Override
    public void setHomes(@NotNull Collection<SHomeBuilder> homes) {
        this.homes.clear();
        homes.forEach(this::register);
    }

    @Override
    public void setPreventTeleportRequests(boolean prevent) {
        this.preventingTeleportRequests = prevent;
    }

    public void applyChangesFrom(@NotNull AbstractProfileData data) {
        this.backTeleportLocations.addAll(data.backTeleportLocations);
        this.displayName = data.displayName;
        this.homes.addAll(data.homes);
        this.releaseFromJail = data.releaseFromJail;
        this.canLooseItemsWhenUsed = data.canLooseItemsWhenUsed;
        this.isInJail = data.isInJail;
        this.mailMessages.addAll(data.mailMessages);
        this.moduleData.addAll(data.moduleData);
        this.preventingTeleportRequests = data.preventingTeleportRequests;
    }
}
