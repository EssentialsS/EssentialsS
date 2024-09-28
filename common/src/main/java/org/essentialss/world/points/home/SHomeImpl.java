package org.essentialss.world.points.home;

import org.essentialss.api.utils.validation.Validator;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.jetbrains.annotations.NotNull;

public class SHomeImpl implements SHome {

    private final @NotNull OfflineLocation location;
    private final @NotNull String homeName;

    public SHomeImpl(@NotNull SHomeBuilder builder) {
        this.location = new Validator<>(builder.point()).notNull().validate();
        this.homeName = new Validator<>(builder.home()).notNull().validate();
    }

    @Override
    public @NotNull String identifier() {
        return this.homeName;
    }

    @Override
    public SHomeBuilder builder() {
        return new SHomeBuilder().setHome(this.identifier()).setPoint(this.location);
    }

    @Override
    public @NotNull OfflineLocation location() {
        return this.location;
    }
}
