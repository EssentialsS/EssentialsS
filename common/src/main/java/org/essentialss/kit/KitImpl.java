package org.essentialss.kit;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.group.Group;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.kit.KitBuilder;
import org.essentialss.api.kit.KitSlot;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.misc.InventoryHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.plugin.PluginContainer;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class KitImpl implements Kit {

    private final @NotNull String idName;
    private final @Nullable String displayName;
    private final @NotNull Singleton<UnmodifiableCollection<KitSlot>> slots;
    private final @NotNull PluginContainer plugin;
    private final @NotNull Map<String, Duration> cooldowns;

    KitImpl(KitBuilder builder) {
        this(builder.plugin(), builder.idName(), builder.displayName(), builder.getCooldowns(),
             () -> builder.kitSlots().entrySet().stream().map(entry -> new KitSlotImpl(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }

    KitImpl(@NotNull PluginContainer container,
            @NotNull String idName,
            @Nullable String displayName,
            @NotNull Map<Group, Duration> cooldowns,
            @NotNull Supplier<Collection<KitSlot>> slots) {
        this(container, idName, displayName, cooldowns, new Singleton<>(() -> new SingleUnmodifiableCollection<>(slots.get())));
    }

    private KitImpl(@NotNull PluginContainer container,
                    @NotNull String idName,
                    @Nullable String displayName,
                    @NotNull Map<Group, Duration> cooldowns,
                    @NotNull Singleton<UnmodifiableCollection<KitSlot>> slots) {
        this.slots = slots;
        this.cooldowns = cooldowns.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().groupName(), Map.Entry::getValue));
        this.plugin = container;
        this.idName = idName;
        this.displayName = displayName;
    }

    @Override
    public Optional<Duration> cooldown(Group group) {
        return Optional.ofNullable(this.cooldowns.get(group.groupName()));
    }

    @Override
    public ViewableInventory createInventory() {
        Collection<KitSlot> slots = this.slots.get();
        Supplier<ContainerType> containerType = InventoryHelper.preferredGenericContainer(slots.size());
        ViewableInventory inv = ViewableInventory.builder().type(containerType).completeStructure().plugin(EssentialsSMain.plugin().container()).build();
        slots.forEach(slot -> inv.offer(slot.item().createStack()));
        return inv;
    }

    @Override
    public String displayName() {
        if (null == this.displayName) {
            return this.idName;
        }
        return this.displayName;
    }

    @Override
    public Singleton<UnmodifiableCollection<KitSlot>> inventory() {
        return this.slots;
    }

    @Override
    public String name() {
        return this.idName;
    }

    @Override
    public PluginContainer plugin() {
        return this.plugin;
    }

    @Override
    public void setCooldown(Group group, Duration duration) {
        this.cooldowns.put(group.groupName(), duration);
    }
}
