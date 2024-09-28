package org.essentialss.world;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.points.spawn.SSpawnPoint;
import org.essentialss.api.world.points.spawn.SSpawnPointBuilder;
import org.essentialss.api.world.points.warp.SWarp;
import org.essentialss.api.world.points.warp.SWarpBuilder;
import org.essentialss.world.points.spawn.SSpawnPointImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3d;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

final class SWorldDataSerializer {

    private SWorldDataSerializer() {
        throw new RuntimeException("Should not create");
    }

    static void load(@NotNull SWorldData worldData) throws ConfigurateException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/world/" + worldData.identifier().replaceAll(":", "/") + ".conf");
        if (!System.getProperty("os.name").contains("Windows")) {
            //backwards compatibility with 0.0.4-
            File legacyFile = new File(folder, "data/world/" + worldData.identifier() + ".conf");
            if (legacyFile.exists()) {
                try {
                    if (!file.exists()) {
                        EssentialsSMain.plugin().logger().warn("Moving " + worldData.identifier() + " due to a Windows bug");
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        Files.move(legacyFile.toPath(), file.toPath());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.load();
        worldData.clearPoints();

        root.node("points").childrenList().forEach(node -> {
            String type = node.node("type").getString();
            if (null == type) {
                return;
            }
            double x = node.node("x").getDouble();
            double y = node.node("y").getDouble();
            double z = node.node("z").getDouble();
            if (type.equalsIgnoreCase("Warp")) {
                String warpName = node.node("name").getString();
                if (null == warpName) {
                    return;
                }
                worldData.register(new SWarpBuilder().setName(warpName).setPoint(new Vector3d(x, y, z)), false, null);
            }
            if (type.equalsIgnoreCase("Spawn")) {
                worldData.register(new SSpawnPointBuilder().setPosition(new Vector3d(x, y, z)), false, null);
            }
        });
    }

    @SuppressWarnings("DuplicateThrows")
    static void save(@NotNull SWorldData worldData) throws ConfigurateException, SerializationException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/world/" + worldData.identifier().replaceAll(":", "/") + ".conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.createNode();

        CommentedConfigurationNode pointNode = root.node("points");
        for (SWarp warp : worldData.warps()) {
            CommentedConfigurationNode points = pointNode.appendListNode();
            points.node("name").set(warp.identifier());
            points.node("type").set("Warp");
            points.node("x").set(warp.position().x());
            points.node("y").set(warp.position().y());
            points.node("z").set(warp.position().z());
        }
        for (SSpawnPoint spawnPoint : worldData.spawnPoints().stream().filter(s -> s instanceof SSpawnPointImpl).collect(Collectors.toList())) {
            CommentedConfigurationNode points = pointNode.appendListNode();
            points.node("type").set("Spawn");
            points.node("x").set(spawnPoint.position().x());
            points.node("y").set(spawnPoint.position().y());
            points.node("z").set(spawnPoint.position().z());
        }
        loader.save(root);

    }

}
