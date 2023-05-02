package org.essentialss.config.loaders;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class ItemStackLoader implements TypeLoader<ItemStack> {
    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        DataView view = TypeLoaders.DATA_VIEW.deserialize(DataView.class, node);
        return ItemStack.builder().fromContainer(view).build();
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        TypeLoaders.DATA_VIEW.serialize(ItemStack.class, obj.createSnapshot().toContainer(), node);
    }

    @Override
    public Class<ItemStack> ofType() {
        return ItemStack.class;
    }
}
