package org.essentialss.config.value.simple;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.group.Group;
import org.essentialss.api.group.GroupManager;
import org.essentialss.misc.OrElse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class GroupConfigNode implements SingleConfigValue<Group> {

    private Object[] nodes;

    public GroupConfigNode(Object... nodes) {
        this.nodes = nodes;
    }

    @NotNull
    @Override
    public Object[] nodes() {
        return this.nodes;
    }

    @SuppressWarnings("allow-nullable")
    @Nullable
    @Override
    public Group parse(@NotNull ConfigurationNode root) throws SerializationException {
        String groupName = root.node(this.nodes()).getString();
        if (groupName == null) {
            return null;
        }
        GroupManager groupManager = EssentialsSMain.plugin().groupManager().get();
        return groupManager.group(groupName).orElseGet(() -> groupManager.register(groupName));
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable Group value) throws SerializationException {
        String groupName = OrElse.mapNull(value, Group::groupName, () -> null);
        root.node(this.nodes).set(groupName);
    }

    @NotNull
    @Override
    public Class<?> type() {
        return Group.class;
    }
}
