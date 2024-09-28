package org.essentialss.misc;

import org.essentialss.api.utils.Constants;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;

import java.util.function.Supplier;

public final class InventoryHelper {

    private InventoryHelper() {
        throw new RuntimeException("Should not generate");
    }

    public static Supplier<ContainerType> preferredGenericContainer(int itemCount) {
        if (Constants.ONE_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X1;
        }
        if (Constants.TWO_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X2;
        }
        if (Constants.THREE_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X3;
        }
        if (Constants.FOUR_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X4;
        }
        if (Constants.FIVE_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X5;
        }
        if (Constants.SIX_ROW_INVENTORY >= itemCount) {
            return ContainerTypes.GENERIC_9X6;
        }
        throw new IndexOutOfBoundsException("Cannot be greater then " + Constants.SIX_ROW_INVENTORY + ". itemCount was " + itemCount);
    }
}
