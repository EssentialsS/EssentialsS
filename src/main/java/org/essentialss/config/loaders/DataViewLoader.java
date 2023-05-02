package org.essentialss.config.loaders;

import org.essentialss.EssentialsSMain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.*;

public class DataViewLoader implements TypeLoader<DataView> {

    DataViewLoader() {

    }

    @Override
    public DataContainer deserialize(Type type, ConfigurationNode node) {
        DataContainer container = DataContainer.createNew();
        for (ConfigurationNode query : node.childrenMap().values()) {
            Map<List<String>, Object> values = this.findValue(query, new ArrayList<>());
            for (Map.Entry<List<String>, Object> entry : values.entrySet()) {
                DataQuery valueQuery = DataQuery.of(entry.getKey());
                container = container.set(valueQuery, entry.getValue());
            }
        }
        return container;
    }

    @Override
    public void serialize(Type type, @Nullable DataView obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        for (DataQuery key : obj.keys(true)) {
            Optional<Object> opValue = obj.get(key);
            if (!opValue.isPresent()) {
                EssentialsSMain.plugin().logger().warn("Skipping '" + key + "'. Could not read value");
                continue;
            }
            Object[] nodes = key.parts().stream().map(s -> (Object) s).toArray();
            Object value = opValue.get();

            node.node(nodes).node("value").set(value);
            node.node(nodes).node("type").set(value.getClass().getTypeName());
        }
    }

    private Map<List<String>, Object> findValue(ConfigurationNode node, List<String> path) {
        List<String> newPath = new ArrayList<>(path);
        Map<List<String>, Object> newMap = new HashMap<>();
        Object key = node.key();
        if (null == key) {
            EssentialsSMain.plugin().logger().warn("Node key is null, skipping");
            return newMap;
        }
        newPath.add(key.toString());
        if (node.node("type").isNull()) {
            for (ConfigurationNode child : node.childrenList()) {
                Map<List<String>, Object> returnedMap = this.findValue(child, newPath);
                newMap.putAll(returnedMap);
            }
            return newMap;
        }
        String type = node.node("type").getString();
        Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Object value = node.node("value").get(clazz);
            newMap.put(newPath, value);
            return newMap;
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<DataView> ofType() {
        return DataView.class;
    }
}
