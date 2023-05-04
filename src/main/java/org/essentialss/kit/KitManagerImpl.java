package org.essentialss.kit;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.kit.KitBuilder;
import org.essentialss.api.kit.KitManager;
import org.essentialss.api.kit.KitSlot;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.config.loaders.TypeLoaders;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.plugin.PluginContainer;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class KitManagerImpl implements KitManager {

    private static final Object[] DISPLAY_NAME_NODE = {"IdName"};
    private static final Object[] ITEMS_NODE = {"items"};
    private static final Object[] ITEM_SLOT_NODE = {"slot"};
    private static final Object[] ITEM_NODE = {"item"};
    private final Collection<Kit> kits = new LinkedHashSet<>();

    @Override
    public UnmodifiableCollection<Kit> kits() {
        if (this.kits.isEmpty()) {
            this.reloadKits();
        }
        return new SingleUnmodifiableCollection<>(this.kits);
    }

    @Override
    public Kit register(@NotNull KitBuilder builder) {
        Kit kit = new KitImpl(builder);
        try {
            this.save(kit);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        this.kits.add(kit);
        return kit;
    }

    @Override
    public void reloadKits() {
        File folder = this.kitFolder();
        File[] pluginFolders = folder.listFiles();
        if (null == pluginFolders) {
            EssentialsSMain.plugin().logger().warn("0 kits loaded. No folder detected");
            return;
        }

        for (File pluginFolder : pluginFolders) {
            File[] files = pluginFolder.listFiles((file, s) -> s.endsWith(".conf"));
            if (null == files) {
                continue;
            }
            for (File file : files) {
                String idName = file.getName().substring(0, file.getName().length() - 5);
                String pluginId = pluginFolder.getName();
                Optional<PluginContainer> opContainer = Sponge.pluginManager().plugin(pluginId);
                if (!opContainer.isPresent()) {
                    EssentialsSMain.plugin().logger().error("Could not load kit of " + idName + ". As plugin '" + pluginId + "' cannot be found. Skipping");
                    continue;
                }
                HoconConfigurationLoader loader = TypeLoaders.applyAll(HoconConfigurationLoader.builder().file(file)).build();
                try {
                    ConfigurationNode node = loader.load();
                    String displayName = node.node(DISPLAY_NAME_NODE).getString();
                    Supplier<Collection<KitSlot>> kitSlots = () -> node.node(ITEMS_NODE).childrenList().stream().map(nodeIndex -> {
                        ConfigurationNode slotIndexNode = nodeIndex.node(ITEM_SLOT_NODE);
                        Integer index = null;
                        if (!slotIndexNode.isNull()) {
                            index = slotIndexNode.getInt();
                        }
                        try {
                            ItemStack item = nodeIndex.node(ITEM_NODE).get(ItemStack.class);
                            if (null == item) {
                                throw new RuntimeException("Could not read item: " + nodeIndex.node(ITEM_NODE).path().toString());
                            }
                            ItemStackSnapshot snapshot = item.createSnapshot();
                            return new KitSlotImpl(snapshot, index);
                        } catch (SerializationException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
                    this.kits.add(new KitImpl(opContainer.get(), idName, displayName, kitSlots));
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        EssentialsSMain.plugin().logger().info("Loaded " + this.kits.size() + " kits");
    }

    @Override
    public void save(@NotNull Kit kit) throws ConfigurateException {
        File file = kit.file();
        HoconConfigurationLoader loader = TypeLoaders.applyAll(HoconConfigurationLoader.builder().file(file)).build();
        CommentedConfigurationNode node = loader.createNode();
        node.node(DISPLAY_NAME_NODE).comment("The name of the kit. Spaces are not allowed.").set(kit.displayName());
        CommentedConfigurationNode itemsNode = node.node(ITEMS_NODE);
        for (KitSlot slot : kit.inventory().get()) {
            CommentedConfigurationNode indexNode = itemsNode.appendListNode();
            if (slot.preferredSlotIndex().isPresent()) {
                indexNode.node(ITEM_SLOT_NODE).comment("The slot the item will be placed in (if possible)").set(slot.preferredSlotIndex().getAsInt());
            }
            indexNode.node(ITEM_NODE).comment("The item's data").set(DataView.class, slot.item().toContainer());
        }
        loader.save(node);
    }
}
