package org.essentialss.player.data;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.config.value.ConfigValue;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.world.points.OfflineLocation;
import org.essentialss.api.world.points.home.SHomeBuilder;
import org.essentialss.config.value.ListConfigValueImpl;
import org.essentialss.config.value.position.HomeConfigValue;
import org.essentialss.config.value.position.LocationConfigValue;
import org.essentialss.config.value.primitive.BooleanConfigValue;
import org.essentialss.config.value.simple.ComponentConfigValue;
import org.essentialss.config.value.simple.DateTimeConfigValue;
import org.essentialss.config.value.simple.EnumConfigValue;
import org.essentialss.config.value.simple.RegistryConfigValue;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class UserDataSerializer {

    private static final ConfigValue<Boolean> CAN_LOOSE_ITEMS_WHEN_USED = new BooleanConfigValue(true, "inventory", "LooseItemsWhenUsed");
    private static final ConfigValue<Boolean> PREVENT_TELEPORT_REQUESTS = new BooleanConfigValue(false, "other", "BlockingTeleportRequests");
    private static final BooleanConfigValue IS_IN_JAIL = new BooleanConfigValue(false, "jail", "In");
    private static final DateTimeConfigValue RELEASED_FROM_JAIL = new DateTimeConfigValue("jail", "ReleasedOn");
    private static final ComponentConfigValue DISPLAY_NAME = new ComponentConfigValue("other", "DisplayName");
    private static final CollectionConfigValue<MuteType> MUTE_TYPES = new ListConfigValueImpl<>(new EnumConfigValue<>(MuteType.class, "chat", "MuteTypes"));
    private static final CollectionConfigValue<OfflineLocation> BACK_LOCATIONS = new ListConfigValueImpl<>(new LocationConfigValue("placement"), "locations",
                                                                                                           "back");
    private static final CollectionConfigValue<SHomeBuilder> HOMES = new ListConfigValueImpl<>(new HomeConfigValue(), "homes");
    private static final CollectionConfigValue<DamageType> IMMUNE_TO = new ListConfigValueImpl<>(RegistryConfigValue.damageType(), "immune_to");

    private UserDataSerializer() {
        throw new RuntimeException("Should not create");
    }

    @SuppressWarnings("DuplicateThrows")
    static void load(SGeneralUnloadedData userData) throws ConfigurateException, SerializationException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/players/" + userData.uuid() + ".conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.load();

        boolean isInJail = IS_IN_JAIL.parseDefault(root);
        if (isInJail && (userData instanceof AbstractProfileData)) {
            AbstractProfileData apd = ((AbstractProfileData) userData);
            LocalDateTime releasedFromJailTime = RELEASED_FROM_JAIL.parse(root);
            apd.isInJail.setValue(true);
            apd.releasedFromJail.setValue(releasedFromJailTime);
        }

        Component displayName = DISPLAY_NAME.parse(root);
        userData.setDisplayName(displayName);

        List<OfflineLocation> backLocations = BACK_LOCATIONS.parse(root);
        userData.setBackTeleportLocations(backLocations);

        List<SHomeBuilder> homes = HOMES.parse(root);
        if (null != homes) {
            userData.setHomes(homes);
        }

        List<MuteType> muteTypes = MUTE_TYPES.parse(root);
        if (null != muteTypes) {
            userData.setMuteTypes(muteTypes.toArray(new MuteType[0]));
        }

        List<DamageType> immuneTo = IMMUNE_TO.parse(root);
        if (null != immuneTo) {
            userData.setImmuneTo(immuneTo);
        }
    }

    @SuppressWarnings("DuplicateThrows")
    static void save(SGeneralUnloadedData userData) throws ConfigurateException, SerializationException {
        File folder = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory().toFile();
        File file = new File(folder, "data/players/" + userData.uuid() + ".conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.createNode();
        IS_IN_JAIL.set(root, userData.isInJail());
        PREVENT_TELEPORT_REQUESTS.set(root, userData.isPreventingTeleportRequests());
        CAN_LOOSE_ITEMS_WHEN_USED.set(root, userData.canLooseItemsWhenUsed());
        RELEASED_FROM_JAIL.set(root, userData.releasedFromJailTime().orElse(null));
        DISPLAY_NAME.set(root, userData.hasSetDisplayName() ? userData.displayName() : null);
        BACK_LOCATIONS.set(root, new LinkedList<>(userData.backTeleportLocations()));
        MUTE_TYPES.set(root, new ArrayList<>(userData.muteTypes()));
        //HOMES.set(root, userData.homes().stream().map(SHome::builder).collect(Collectors.toList()));
        IMMUNE_TO.set(root, new LinkedList<>(userData.immuneTo()));
        loader.save(root);
    }
}
