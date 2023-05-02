package org.essentialss.config.value.position;

import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.config.value.simple.StringConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class HomeConfigValue implements SingleConfigValue<SHomeBuilder> {

    private static final LocationConfigValue LOCATION = new LocationConfigValue("location");
    private static final StringConfigValue NAME = new StringConfigValue("name");

    private final Object[] objects;

    public HomeConfigValue() {
        this(new Object[0]);
    }

    public HomeConfigValue(Object... objects) {
        this.objects = objects;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.objects;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable SHomeBuilder parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        String name = NAME.parse(node);
        OfflineLocation loc = LOCATION.parse(node);
        if ((null == name) || (null == loc)) {
            return null;
        }

        return new SHomeBuilder().setHome(name).setPoint(loc);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable SHomeBuilder value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }

        OfflineLocation loc = value.point();
        if ((null == loc)) {
            throw new SerializationException("Location is missing");
        }

        NAME.set(node, value.home());
        LOCATION.set(node, loc);


    }

    @Override
    public @NotNull Class<?> type() {
        return SHomeBuilder.class;
    }
}
