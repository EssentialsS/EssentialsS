package org.essentialss.implementation.config.value.position;

import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.client.ClientWorld;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3d;

public class LocationConfigValue implements SingleConfigValue<OfflineLocation> {

    private final Object[] node;

    private static final String X_PATH = "X";
    private static final String Y_PATH = "Y";
    private static final String Z_PATH = "Z";

    private static final String WORLD_PATH = "World";

    public LocationConfigValue() {
        this(new Object[0]);
    }

    public LocationConfigValue(Object... nodes) {
        this.node = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.node;
    }

    @Override
    public @NotNull Class<OfflineLocation> type() {
        return OfflineLocation.class;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable OfflineLocation parse(@NotNull ConfigurationNode root) {
        ConfigurationNode node = root.node(this.nodes());
        double x = node.node(X_PATH).getDouble();
        double y = node.node(Y_PATH).getDouble();
        double z = node.node(Z_PATH).getDouble();
        String world = node.node(WORLD_PATH).getString();

        if (null == world) {
            return null;
        }
        if (Sponge.isServerAvailable()) {
            ResourceKey key = ResourceKey.resolve(world);
            return new OfflineLocation(key, new Vector3d(x, y, z));
        }

        ClientWorld clientWorld = Sponge
                .client()
                .world()
                .orElseThrow(() -> new IllegalStateException("Client world is not present"));
        if (!clientWorld.context().toString().equals(world)) {
            throw new IllegalStateException("World is not loaded -> Client world must be loaded");
        }
        return new OfflineLocation(EssentialsSMain.plugin().worldManager().get().dataFor(clientWorld),
                                   new Vector3d(x, y, z));
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable OfflineLocation value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }
        node.node(X_PATH).set(value.position().x());
        node.node(Y_PATH).set(value.position().y());
        node.node(Z_PATH).set(value.position().z());
        node.node(WORLD_PATH).set(value.identifier());
    }
}
