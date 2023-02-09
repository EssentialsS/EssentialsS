package org.essentialss.implementation.player.data;

import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.player.mail.MailMessage;
import org.essentialss.api.player.mail.MailMessageBuilder;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.world.points.home.SHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.world.Location;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

public abstract class AbstractUserData implements SGeneralOfflineData {

    protected boolean canLooseItemsWhenUsed;
    protected boolean muted;
    protected boolean isInJail;
    protected @Nullable LocalDateTime releaseFromJail;
    protected LinkedList<Location<?, ?>> backTeleportLocations = new LinkedList<>();
    protected LinkedList<MailMessage> mailMessages = new LinkedList<>();

    protected boolean preventingTeleportRequests;

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
        return null;
    }

    @Override
    public @NotNull LinkedList<Location<?, ?>> backTeleportLocations() {
        return this.backTeleportLocations;
    }

    @Override
    public void setBackTeleportLocations(Collection<Location<?, ?>> locations) {
        this.backTeleportLocations.clear();
        this.backTeleportLocations.addAll(locations);
    }

    @Override
    public void addBackTeleportLocation(@NotNull Location<?, ?> location) {
        this.backTeleportLocations.add(location);
    }

    @Override
    public void removeBackTeleportLocation(@NotNull Location<?, ?> location) {
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
