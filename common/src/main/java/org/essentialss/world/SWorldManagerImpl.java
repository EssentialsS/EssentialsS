package org.essentialss.world;

import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.SWorldManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class SWorldManagerImpl implements SWorldManager {

    private final Collection<SWorldData> worldData = new LinkedHashSet<>();

    @Override
    public @NotNull SWorldData dataFor(@NotNull World<?, ?> worldData) {
        Optional<SWorldData> opData = this.worldData.parallelStream().filter(data -> data.isWorld(worldData)).findAny();
        if (opData.isPresent()) {
            return opData.get();
        }
        SWorldData data = new SWorldDataImpl(new SWorldDataBuilder().setWorld(worldData));
        try {
            data.reloadFromConfig();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        this.worldData.add(data);
        return data;
    }

    @Override
    public @NotNull SWorldData dataFor(@NotNull ResourceKey worldKey) {
        Optional<SWorldData> opData = this.worldData
                .parallelStream()
                .filter(data -> data.identifier().equalsIgnoreCase(worldKey.formatted()))
                .findAny();
        if (opData.isPresent()) {
            return opData.get();
        }
        SWorldData data = new SWorldDataImpl(new SWorldDataBuilder().setWorldKey(worldKey));
        try {
            data.reloadFromConfig();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        this.worldData.add(data);
        return data;
    }
}
