package org.essentialss.implementation.player.data;

import net.kyori.adventure.text.Component;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.mail.MailMessage;
import org.essentialss.api.player.mail.MailMessageBuilder;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.implementation.world.points.home.SHomeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;

public abstract class AbstractProfileData implements SGeneralUnloadedData {

    final LinkedList<OfflineLocation> backTeleportLocations = new LinkedList<>();
    private final LinkedList<MailMessage> mailMessages = new LinkedList<>();
    private final @NotNull LinkedTransferQueue<SHome> homes = new LinkedTransferQueue<>();
    boolean isInJail;
    @Nullable LocalDateTime releaseFromJail;
    private boolean canLooseItemsWhenUsed;
    @Nullable
    private Component displayName;
    private boolean muted;
    private boolean preventingTeleportRequests;

    public void applyChangesFrom(@NotNull AbstractProfileData data) {
        this.backTeleportLocations.addAll(data.backTeleportLocations);
        this.displayName = data.displayName;
        this.homes.addAll(data.homes);
        this.releaseFromJail = data.releaseFromJail;
        this.canLooseItemsWhenUsed = data.canLooseItemsWhenUsed;
        this.isInJail = data.isInJail;
        this.mailMessages.addAll(data.mailMessages);
        this.muted = data.muted;
        this.preventingTeleportRequests = data.preventingTeleportRequests;
    }

    @Override
    public @NotNull Component displayName() {
        if (null == this.displayName) {
            return Component.text(this.playerName());
        }
        return this.displayName;

    }

    @Override
    public boolean hasSetDisplayName() {
        return null != this.displayName;
    }

    @Override
    public void setDisplayName(@Nullable Component component) {
        this.displayName = component;
    }

    @Override
    public boolean canLooseItemsWhenUsed() {
        return this.canLooseItemsWhenUsed;
    }

    @Override
    public void setCanLooseItemsWhenUsed(boolean check) {
        this.canLooseItemsWhenUsed = check;
    }

    @Override
    public boolean muted() {
        return this.muted;
    }

    @Override
    public void setMuted(boolean mute) {
        this.muted = mute;
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
    public void setPreventTeleportRequests(boolean prevent) {
        this.preventingTeleportRequests = prevent;
    }

    @Override
    public Optional<LocalDateTime> releasedFromJailTime() {
        return Optional.ofNullable(this.releaseFromJail);
    }

    @Override
    public @NotNull UnmodifiableCollection<SHome> homes() {
        throw new RuntimeException("homes not implemented yet");
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
    public void setHomes(@NotNull Collection<SHomeBuilder> homes) {
        this.homes.clear();
        homes.forEach(this::register);
    }

    @Override
    public @NotNull LinkedList<OfflineLocation> backTeleportLocations() {
        return this.backTeleportLocations;
    }

    @Override
    public void setBackTeleportLocations(Collection<OfflineLocation> locations) {
        this.backTeleportLocations.clear();
        this.backTeleportLocations.addAll(locations);
    }

    @Override
    public void addBackTeleportLocation(@NotNull OfflineLocation location) {
        this.backTeleportLocations.add(location);
    }

    @Override
    public void removeBackTeleportLocation(@NotNull OfflineLocation location) {
        this.backTeleportLocations.remove(location);
    }

    @Override
    public @NotNull UnmodifiableCollection<MailMessage> mailMessages() {
        return new UnmodifiableCollection<>(this.mailMessages);
    }

    @Override
    public void removeMessage(@NotNull MailMessage message) {
        this.mailMessages.remove(message);
    }

    @Override
    public void addMailMessage(@NotNull MailMessageBuilder builder) {
        throw new RuntimeException("Not implemented yet");
    }
}
