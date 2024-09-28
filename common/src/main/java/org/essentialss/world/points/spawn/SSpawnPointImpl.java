package org.essentialss.world.points.spawn;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.api.utils.validation.ValidationRules;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.spawn.SSpawnType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SSpawnPointImpl implements SSpawnPoint {

    private final @NotNull SWorldData world;
    private final @NotNull Supplier<Vector3d> position;
    private final @NotNull Collection<SSpawnType> types;

    public SSpawnPointImpl(@NotNull SSpawnPointBuilder builder, @NotNull SWorldData data) {
        this.position = Objects.requireNonNull(builder.position());
        this.world = data;
        this.types = new Validator<>(
                (Collection<SSpawnType>) builder.spawnTypes().stream().filter(type -> SSpawnType.MAIN_SPAWN != type).collect(Collectors.toList()))
                .notNull()
                .rule(ValidationRules.isSizeGreaterThan(0))
                .validate();
    }

    @Override
    public @NotNull OfflineLocation location() {
        return new OfflineLocation(this.world, this.position.get());
    }

    @Override
    public UnmodifiableCollection<SSpawnType> types() {
        Collection<SSpawnType> types = new ArrayList<>(this.types);
        return new SingleUnmodifiableCollection<>(types);
    }

    @Override
    public SSpawnPointBuilder builder() {
        return new SSpawnPointBuilder().setPosition(this.position).setSpawnTypes(this.types);
    }
}
