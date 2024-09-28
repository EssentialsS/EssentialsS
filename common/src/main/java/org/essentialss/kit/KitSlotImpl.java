package org.essentialss.kit;

import org.essentialss.api.kit.KitSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.OptionalInt;

public class KitSlotImpl implements KitSlot {

    private final @NotNull ItemStackSnapshot stack;
    private final @Nullable Integer slotIndex;

    public KitSlotImpl(@NotNull ItemStackSnapshot stack, @Nullable Integer slotIndex) {
        this.slotIndex = slotIndex;
        this.stack = stack;
    }

    @Override
    public ItemStackSnapshot item() {
        return this.stack;
    }

    @Override
    public OptionalInt preferredSlotIndex() {
        if (null == this.slotIndex) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(this.slotIndex);
    }
}
