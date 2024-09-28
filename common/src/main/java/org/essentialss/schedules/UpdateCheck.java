package org.essentialss.schedules;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class UpdateCheck implements Runnable {

    private static final String VERSION_NAME = "name";
    private static final String VERSION_DEPENDS_ON = "dependencies";
    private static final String VERSION_ID = "id";
    private static final String VERSION_CREATED_AT = "createdAt";
    private static final String VERSION_SIZE_IN_BYTES = "fileSize";
    private static final String VERSION_STAFF_APPROVED = "staffApproved";
    private static final Object[] VERSION_CHANNEL_NAME = {"channel", "name"};
    private static final String DEPENDS_ID = "pluginId";
    private static final String DEPENDS_VERSION = "version";

    private static class DependencyEntry {
        private String name;
        private String version;
    }

    private static class VersionEntry {

        private String channelName;
        private LocalDateTime createdAt;
        private Collection<DependencyEntry> dependencies = new LinkedList<>();
        private int id;
        private boolean isStaffApproved;
        private long sizeInBytes;
        private String url;
        private String versionName;
    }

    private final Audience audience;

    private UpdateCheck(@NotNull Audience audience) {
        this.audience = audience;
    }

    private void howFarOutOfDate(VersionEntry latest, List<VersionEntry> versionsNode) {
        int outOfDateBy = versionsNode.indexOf(latest);
        Component component;
        if (0 == outOfDateBy) {
            component = Component.text("You are on the latest version");
        } else {
            Duration duration = Duration.between(latest.createdAt, versionsNode.get(outOfDateBy).createdAt);
            component = Component.text("You are out of date by " + outOfDateBy + " versions. Estimated " + duration.toDays() + " days between versions");
        }
        this.audience.sendMessage(component);
    }

    @Override
    public void run() {
        try {
            PluginMetadata container = EssentialsSMain.plugin().container().metadata();
            URL getVersionsUrl = new URL("https://ore.spongepowered.org/api/v1/projects/" + container.id() + "/versions");
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().url(getVersionsUrl).build();
            BasicConfigurationNode root = loader.load();

            PluginMetadata apiMetadata = Sponge.platform().container(Platform.Component.API).metadata();

            List<VersionEntry> versionsNode = root
                    .childrenList()
                    .stream()
                    .map(UpdateCheck::create)
                    .filter(entry -> entry.dependencies.stream().filter(depend -> depend.name.equals(apiMetadata.id())).anyMatch(depend -> {
                        int firstNumberIndex = depend.version.indexOf(".");
                        String firstNumberString = depend.version.substring(0, firstNumberIndex);
                        int firstNumber = Integer.parseInt(firstNumberString);
                        return firstNumber >= apiMetadata.version().getMajorVersion();
                    }))
                    .sorted(Comparator.comparing(entry -> ((VersionEntry)entry).createdAt).reversed())
                    .collect(Collectors.toList());
            if (versionsNode.isEmpty()) {
                Component component = Component.text("No valid updates for you");
                this.audience.sendMessage(component);
            }
            Optional<VersionEntry> opCurrentVersionNode = versionsNode
                    .stream()
                    .filter(node -> node.versionName.equals(container.version().toString()))
                    .findAny();
            VersionEntry latest = opCurrentVersionNode.orElseGet(() -> create(container));
            if (!opCurrentVersionNode.isPresent()) {
                Component component = Component.text(
                        "Unable to find this version. Latest on ore.spongepowered.org is version: " + versionsNode.get(0).versionName);
                this.audience.sendMessage(component);
                return;
            }
            this.howFarOutOfDate(latest, versionsNode);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static VersionEntry create(Inheritable metadata) {
        VersionEntry entry = new VersionEntry();
        entry.versionName = metadata.version().toString();
        entry.id = -1;
        entry.createdAt = LocalDateTime.now();
        entry.dependencies = metadata.dependencies().stream().map(pd -> {
            DependencyEntry depends = new DependencyEntry();
            depends.name = pd.id();
            depends.version = pd.version().toString();
            return depends;
        }).collect(Collectors.toList());
        entry.channelName = "unknown";
        return entry;
    }

    private static VersionEntry create(ConfigurationNode node) {
        VersionEntry entry = new VersionEntry();
        entry.versionName = node.node(VERSION_NAME).getString();
        entry.id = node.node(VERSION_ID).getInt();
        String createdAtString = node.node(VERSION_CREATED_AT).getString();
        if (null != createdAtString) {
            entry.createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        }
        for (ConfigurationNode dependNode : node.node(VERSION_DEPENDS_ON).childrenList()) {
            DependencyEntry dependency = new DependencyEntry();
            dependency.name = dependNode.node(DEPENDS_ID).getString();
            dependency.version = dependNode.node(DEPENDS_VERSION).getString();
            entry.dependencies.add(dependency);
        }
        entry.sizeInBytes = node.node(VERSION_SIZE_IN_BYTES).getLong();
        entry.channelName = node.node(VERSION_CHANNEL_NAME).getString();
        entry.isStaffApproved = node.node(VERSION_STAFF_APPROVED).getBoolean();
        return entry;
    }

    public static void createDelay(@NotNull Audience audience) {
        Sponge.asyncScheduler().executor(EssentialsSMain.plugin().container()).execute(new UpdateCheck(audience));
    }
}
