package org.essentialss.implementation.world;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.validation.ValidationRules;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.SPoint;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.api.world.points.jail.SJailSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.essentialss.implementation.world.points.spawn.DefaultSpawnPoint;
import org.essentialss.implementation.world.points.spawn.SSpawnPointImpl;
import org.essentialss.implementation.world.points.warps.SWarpsImpl;
import org.jetbrains.annotations.NotNull;
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
    public void register(@NotNull SHomeBuilder builder) {
        throw new RuntimeException("home is not implemented");
    }

    @Override
    public void register(@NotNull SSpawnPointBuilder builder) {
        if (SSpawnType.WORLD_ASSIGNED == builder.spawnType()) {
            this.world.properties().setSpawnPosition(builder.point().toInt());
            return;
        }
        this.points.add(new SSpawnPointImpl(builder, this));
    }

    @Override
    public void register(@NotNull SWarpBuilder builder) {
        this.points.add(new SWarpsImpl(builder, this));
    }

    @Override
    public void register(@NotNull SJailSpawnPoint builder) {
        throw new RuntimeException("Jail is not implemented");
    }
}
