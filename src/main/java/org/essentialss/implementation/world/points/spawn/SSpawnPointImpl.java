package org.essentialss.implementation.world.points.spawn;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
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
import java.util.List;
import java.util.stream.Collectors;

public class SSpawnPointImpl implements SSpawnPoint {

    private final @NotNull OfflineLocation location;
    private final @NotNull Collection<SSpawnType> types;

    public SSpawnPointImpl(@NotNull SSpawnPointBuilder builder, @NotNull SWorldData data) {
        Vector3d vector = new Validator<>(builder.point()).notNull().validate();
        this.location = data.offlineLocation(vector);
        this.types = new Validator<>(builder.spawnTypes())
                .notNull()
                .rule(ValidationRules.isSizeGreaterThan(0))
                .validate()
                .stream()
                .filter(type -> SSpawnType.MAIN_SPAWN != type)
                .collect(Collectors.toList());
    }

    @Override
    public UnmodifiableCollection<SSpawnType> types() {
        List<SSpawnType> types = new ArrayList<>(this.types);
        this.location.world().ifPresent(sWorld -> {
            if (sWorld.properties().spawnPosition().equals(this.location.position().toInt())) {
                types.add(SSpawnType.MAIN_SPAWN);
            }
        });
        return new UnmodifiableCollection<>(types);
    }

    @Override
    public SSpawnPointBuilder builder() {
        return new SSpawnPointBuilder().setPoint(this.location.position()).setSpawnTypes(this.types);
    }

    @Override
    public @NotNull OfflineLocation location() {
        return this.location;
    }
}
