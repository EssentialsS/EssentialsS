package org.essentialss.implementation.world;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.validation.ValidationRules;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.api.world.points.jail.SJailSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.essentialss.implementation.events.point.register.RegisterPointPostEventImpl;
import org.essentialss.implementation.events.point.register.RegisterPointPreEventImpl;
import org.essentialss.implementation.world.points.spawn.DefaultSpawnPoint;
import org.essentialss.implementation.world.points.spawn.SSpawnPointImpl;
import org.essentialss.implementation.world.points.warps.SWarpsImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class SWorldDataImpl implements SWorldData {

    private final World<?, ?> world;
    private final Collection<SPoint> points = new LinkedHashSet<>();

    public SWorldDataImpl(@NotNull SWorldDataBuilder builder) {
        this.world = new Validator<World<?, ?>>(builder.world()).rule(ValidationRules.notNull()).validate();
        this.points.addAll(new Validator<>(builder.points()).rule(ValidationRules.notNull()).validate());
    }

    @Override
    public @NotNull World<?, ?> spongeWorld() {
        return this.world;
    }

    @Override
    public @NotNull UnmodifiableCollection<SPoint> points() {
        LinkedList<SPoint> list = new LinkedList<>(this.points);
        list.add(new DefaultSpawnPoint(this));
        return new UnmodifiableCollection<>(list);
    }

    @Override
    public boolean register(@NotNull SHomeBuilder builder, boolean runEvent, @Nullable Cause cause) {
        throw new RuntimeException("home is not implemented");
    }

    @Override
    public boolean register(@NotNull SSpawnPointBuilder builder, boolean runEvent, @Nullable Cause cause) {
        new Validator<>(builder.point()).notNull().validate();
        SSpawnPointImpl spawnPoint = new SSpawnPointImpl(builder, this);
        if (SSpawnType.WORLD_ASSIGNED == builder.spawnType()) {
            if (runEvent) {
                if (null == cause) {
                    throw new IllegalArgumentException("Cause cannot be null when running events");
                }
                RegisterPointPreEventImpl preEvent = new RegisterPointPreEventImpl(spawnPoint, cause);
                Sponge.eventManager().post(preEvent);
                if (preEvent.isCancelled()) {
                    return false;
                }
            }
            this.world.properties().setSpawnPosition(builder.point().toInt());
            return true;
        }
        return this.register(spawnPoint, runEvent, cause);
    }

    @Override
    public boolean register(@NotNull SWarpBuilder builder, boolean runEvent, @Nullable Cause cause) {
        return this.register(new SWarpsImpl(builder, this), runEvent, cause);
    }

    @Override
    public boolean register(@NotNull SJailSpawnPointBuilder builder, boolean runEvent, @Nullable Cause cause) {
        throw new RuntimeException("Jail is not implemented");
    }

    private boolean register(@NotNull SPoint point, boolean runEvents, @Nullable Cause cause) {
        if (runEvents) {
            if (null == cause) {
                throw new IllegalArgumentException("Cause cannot be null when running events");
            }
            RegisterPointPreEventImpl preEvent = new RegisterPointPreEventImpl(point, cause);
            Sponge.eventManager().post(preEvent);
            if (preEvent.isCancelled()) {
                return false;
            }
        }
        boolean added = this.points.add(point);
        if (runEvents) {
            Event postEvent = new RegisterPointPostEventImpl(point, cause);
            Sponge.eventManager().post(postEvent);
        }
        return added;

    }
}
