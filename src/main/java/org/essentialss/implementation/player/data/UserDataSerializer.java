package org.essentialss.implementation.player.data;

import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHome;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.config.value.ListDefaultConfigValueImpl;
import org.essentialss.implementation.config.value.position.HomeConfigValue;
import org.essentialss.implementation.config.value.position.LocationConfigValue;
import org.essentialss.implementation.config.value.primitive.BooleanConfigValue;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class UserDataSerializer {

    private static final BooleanConfigValue IS_MUTED = new BooleanConfigValue(false, "chat", "muted");
    private static final CollectionConfigValue<OfflineLocation> BACK_LOCATIONS = new ListDefaultConfigValueImpl<>(
            new LocationConfigValue("placement"), "locations", "back");
    private static final CollectionConfigValue<SHomeBuilder> HOMES = new ListDefaultConfigValueImpl<>(
            new HomeConfigValue(), "homes");

    public static void save(SGeneralOfflineData userData) throws SerializationException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/" + userData.uuid());
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.createNode();
        IS_MUTED.set(root, userData.muted());
        BACK_LOCATIONS.set(root, userData.backTeleportLocations());
    }

    public static void load(SGeneralOfflineData userData) throws ConfigurateException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/" + userData.uuid());
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.load();

        boolean isMuted = IS_MUTED.parseDefault(root);
        userData.setMuted(isMuted);

        List<OfflineLocation> backLocations = BACK_LOCATIONS.parse(root);
        BACK_LOCATIONS.set(root, backLocations);

        HOMES.set(root, userData.homes().stream().map(SHome::builder).collect(Collectors.toList()));
    }
}
