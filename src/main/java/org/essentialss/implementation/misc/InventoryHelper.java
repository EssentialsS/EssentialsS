package org.essentialss.implementation.misc;

import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;

import java.util.function.Supplier;

public class InventoryHelper {

    public static Supplier<ContainerType> preferredGenericContainer(int itemCount) {
        if (9 >= itemCount) {
            return ContainerTypes.GENERIC_9X1;
        }
        if (18 >= itemCount) {
            return ContainerTypes.GENERIC_9X2;
        }
        if (27 >= itemCount) {
            return ContainerTypes.GENERIC_9X3;
        }
        if (36 >= itemCount) {
            return ContainerTypes.GENERIC_9X4;
        }
        if (45 >= itemCount) {
            return ContainerTypes.GENERIC_9X5;
        }
        if (54 >= itemCount) {
            return ContainerTypes.GENERIC_9X6;
        }
        throw new IndexOutOfBoundsException("Cannot be greater then 54. itemCount was " + itemCount);
    }
}
