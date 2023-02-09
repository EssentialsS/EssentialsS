package org.essentialss.implementation.world.points.spawn;

import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.validation.ValidationRules;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.SWorldData;
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

    private final @NotNull SWorldData worldData;
    private final @NotNull Vector3d position;
    private final @NotNull Collection<SSpawnType> types;

    public SSpawnPointImpl(@NotNull SSpawnPointBuilder builder, @NotNull SWorldData data) {
        this.worldData = data;
        this.position = new Validator<>(builder.point()).notNull().validate();
        this.types = new Validator<>(builder.spawnTypes())
                .notNull()
                .rule(ValidationRules.isSizeGreaterThan(0))
                .validate()
                .stream()
                .filter(type -> SSpawnType.MAIN_SPAWN != type)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull SWorldData worldData() {
        return this.worldData;
    }

    @Override
    public @NotNull Vector3d position() {
        return this.position;
    }

    @Override
    public UnmodifiableCollection<SSpawnType> types() {
        List<SSpawnType> types = new ArrayList<>(this.types);
        if (this.worldData.spongeWorld().properties().spawnPosition().equals(this.position.toInt())) {
            types.add(SSpawnType.MAIN_SPAWN);
        }
        return new UnmodifiableCollection<>(types);
    }
}
