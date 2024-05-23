package org.essentialss.player.data;

import net.kyori.adventure.text.Component;
import org.essentialss.api.events.world.points.RegisterPointEvent;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.data.module.ModuleData;
import org.essentialss.api.player.mail.MailMessage;
import org.essentialss.api.player.mail.MailMessageBuilder;
import org.essentialss.api.utils.arrays.OrderedUnmodifiableCollection;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleOrderedUnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.events.point.register.RegisterPointPostEventImpl;
import org.essentialss.events.point.register.RegisterPointPreEventImpl;
import org.essentialss.misc.CollectionHelper;
import org.essentialss.world.points.home.SHomeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;
import org.mose.property.impl.nevernull.ReadOnlyNeverNullPropertyImpl;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public abstract class AbstractProfileData implements SGeneralUnloadedData {

    final WriteNeverNullPropertyImpl<Boolean, Boolean> isInJail = WriteNeverNullPropertyImpl.bool();
    final Property.Write<LocalDateTime, LocalDateTime> releasedFromJail = new WritePropertyImpl<>(t -> t, null);
    private final WriteNeverNullPropertyImpl<Boolean, Boolean> canLooseItemsWhenUsed = WriteNeverNullPropertyImpl.bool(true);
    private final Property.Write<Component, Component> displayName = new WritePropertyImpl<>(t -> t, null);
    private final WriteNeverNullPropertyImpl<Boolean, Boolean> isCommandSpying = WriteNeverNullPropertyImpl.bool();
    private final WriteNeverNullPropertyImpl<Boolean, Boolean> isPreventingTeleportRequests = WriteNeverNullPropertyImpl.bool();
    private final WriteNeverNullPropertyImpl<Boolean, Boolean> unlimitedFood = WriteNeverNullPropertyImpl.bool();
    @SuppressWarnings("TypeMayBeWeakened")
    private final ReadOnlyNeverNullPropertyImpl<Boolean, Boolean> hasGodMode = new ReadOnlyNeverNullPropertyImpl<>(t -> t, () -> false, null);
    private final CollectionProperty.Write<OfflineLocation, OrderedUnmodifiableCollection<OfflineLocation>> backTeleportLocations;
    private final CollectionProperty.Write<MailMessage, UnmodifiableCollection<MailMessage>> mailMessages;
    private final CollectionProperty.Write<SHome, UnmodifiableCollection<SHome>> homes;
    private final CollectionProperty.Write<DamageType, UnmodifiableCollection<DamageType>> immuneTo;
    private final CollectionProperty.Write<MuteType, UnmodifiableCollection<MuteType>> muteTypes;
    private final CollectionProperty.Write<ModuleData<?>, LinkedTransferQueue<ModuleData<?>>> moduleData;

    private final Map<Property.Write<?, ?>, Property.ReadOnly<?, ?>> readOnlyProperties = new ConcurrentHashMap<>();

    AbstractProfileData() {
        this.backTeleportLocations = this.orderedCollectionProperty(new LinkedTransferQueue<>());
        this.mailMessages = this.collectionProperty(new LinkedTransferQueue<>());
        this.homes = this.collectionProperty(new LinkedTransferQueue<>());
        this.immuneTo = this.collectionProperty(new LinkedTransferQueue<>());
        this.moduleData = new WriteCollectionPropertyImpl<>(LinkedTransferQueue::new, LinkedTransferQueue::new, new LinkedTransferQueue<>());
        this.muteTypes = this.collectionProperty(new LinkedTransferQueue<>());
        this.hasGodMode.bindTo(this.immuneTo, immuneTo -> {
            Collection<DamageType> types = DamageTypes.registry().stream().collect(Collectors.toCollection(LinkedHashSet::new));
            types.remove(DamageTypes.VOID.get());
            return CollectionHelper.match(immuneTo, types);
        });

        this.moduleData.registerCollectionAddEvent((collectionProperty, before, adding) -> {
            List<ModuleData<?>> toRemove = adding
                    .stream()
                    .filter(data -> before.stream().anyMatch(data2 -> data2.key().equals(data.key())))
                    .collect(Collectors.toList());
            this.moduleData.removeAll(toRemove);
        });
    }

    @Override
    public void addMailMessage(@NotNull MailMessageBuilder builder) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public @NotNull CollectionProperty.Write<OfflineLocation, OrderedUnmodifiableCollection<OfflineLocation>> backTeleportLocationsProperty() {
        return this.backTeleportLocations;
    }

    @Override
    public <P extends Property.Write<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P canLooseItemsWhenUsedProperty() {
        return (P) this.canLooseItemsWhenUsed;
    }

    @Override
    public void deregister(@NotNull SHome home) {
        this.homes.remove(home);
    }

    @Override
    public @NotNull Property.Write<Component, Component> displayNameProperty() {
        return this.displayName;
    }

    @Override
    public <P extends Property.ReadOnly<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P hasGodModeProperty() {
        return (P) this.hasGodMode;
    }

    @Override
    public CollectionProperty.ReadOnly<SHome, UnmodifiableCollection<SHome>> homesProperty() {
        return this.getReadOnly(this.homes);
    }

    @Override
    public CollectionProperty.Write<DamageType, UnmodifiableCollection<DamageType>> immuneToProperty() {
        return this.immuneTo;
    }

    @Override
    public <P extends Property.Write<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P isCommandSpyingProperty() {
        return (P) this.isCommandSpying;
    }

    @Override
    public <P extends Property.ReadOnly<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P isInJailProperty() {
        return this.getReadOnly(this.isInJail);
    }

    @Override
    public <P extends Property.Write<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P isPreventingTeleportRequestsProperty() {
        return (P) this.isPreventingTeleportRequests;
    }

    @Override
    public CollectionProperty.ReadOnly<MailMessage, UnmodifiableCollection<MailMessage>> mailMessagesProperty() {
        return this.getReadOnly(this.mailMessages);
    }

    @Override
    public CollectionProperty.Write<ModuleData<?>, LinkedTransferQueue<ModuleData<?>>> moduleDataProperty() {
        return this.moduleData;
    }

    @Override
    public CollectionProperty.Write<MuteType, UnmodifiableCollection<MuteType>> muteTypesProperty() {
        return this.muteTypes;
    }

    @Override
    public Optional<SHome> register(@NotNull SHomeBuilder builder, @Nullable Cause cause) {
        SHome home = new SHomeImpl(builder);
        if (null != cause) {
            RegisterPointEvent.Pre preEvent = new RegisterPointPreEventImpl(home, cause.with(this));
            Sponge.eventManager().post(preEvent);
            if (preEvent.isCancelled()) {
                return Optional.empty();
            }
        }

        if (this.homes.add(home)) {
            return Optional.of(home);
        }

        if (null != cause) {
            Event postEvent = new RegisterPointPostEventImpl(home, cause.with(this));
            Sponge.eventManager().post(postEvent);
        }
        return Optional.empty();
    }

    @Override
    public Property.ReadOnly<LocalDateTime, LocalDateTime> releasedFromJailTimeProperty() {
        return this.getReadOnly(this.releasedFromJail);
    }

    @Override
    public void removeMessage(@NotNull MailMessage message) {
        this.mailMessages.remove(message);
    }

    @Override
    public void setHomes(@NotNull Collection<SHomeBuilder> homes) {
        List<SHome> homeSet = homes.stream().map(SHomeImpl::new).collect(Collectors.toList());
        this.homes.setValue(homeSet);
    }

    @Override
    public <P extends Property.Write<Boolean, Boolean> & Property.NeverNull<Boolean, Boolean>> P unlimitedFoodProperty() {
        return (P) this.unlimitedFood;
    }

    public void applyChangesFrom(@NotNull AbstractProfileData data) {
        try {
            for (Field field : AbstractProfileData.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object thisValue = field.get(this);
                Object otherValue = field.get(data);
                //properties
                if (thisValue instanceof CollectionProperty.Write) {
                    Collection<?> otherCollectionValue = ((Property.NeverNull<Collection<?>, ? extends Collection<?>>) otherValue).safeValue();
                    //noinspection unchecked,rawtypes
                    ((CollectionProperty.Write) thisValue).addAll(otherCollectionValue);
                }
                if (thisValue instanceof Property.Write) {
                    Optional<?> opOtherValue = ((Property<?, ?>) otherValue).value();
                    opOtherValue.ifPresent(oValue -> this.applyPropertyFrom((Property.Write<?, ?>) thisValue, oValue));
                }


                if (thisValue instanceof Collection) {
                    //noinspection unchecked,rawtypes
                    ((Collection) thisValue).addAll((Collection) otherValue);
                    continue;
                }
                field.set(this, otherValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void applyPropertyFrom(Property.Write<?, ?> property, T value) {
        ((Property.Write<T, ?>) property).setValue(value);
    }

    private <X> WriteCollectionPropertyImpl<X, UnmodifiableCollection<X>> collectionProperty(Collection<X> collection) {
        return new WriteCollectionPropertyImpl<>(SingleUnmodifiableCollection::new, () -> new SingleUnmodifiableCollection<>(Collections.emptyList()),
                                                 collection);
    }

    <T, D, P extends Property.ReadOnly<T, D>> P getReadOnly(Property.Write<T, D> writable) {
        Property.ReadOnly<?, ?> readOnly = this.readOnlyProperties.get(writable);
        if (null != readOnly) {
            return (P) readOnly;
        }
        Property.ReadOnly<T, D> newProp = writable.createBoundReadOnly();
        this.readOnlyProperties.put(writable, newProp);
        return (P) newProp;
    }

    private <X> WriteCollectionPropertyImpl<X, OrderedUnmodifiableCollection<X>> orderedCollectionProperty(Collection<X> collection) {
        return new WriteCollectionPropertyImpl<>(SingleOrderedUnmodifiableCollection::new,
                                                 () -> new SingleOrderedUnmodifiableCollection<>(Collections.emptyList()), collection);
    }
}
