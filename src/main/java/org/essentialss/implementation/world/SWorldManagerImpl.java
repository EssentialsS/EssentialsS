package org.essentialss.implementation.world;

import org.essentialss.api.world.SWorldData;
import org.essentialss.api.world.SWorldManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class SWorldManagerImpl implements SWorldManager {

    private final Collection<SWorldData> worldData = new LinkedHashSet<>();

    @Override
    public @NotNull SWorldData dataFor(@NotNull World<?, ?> worldData) {
        Optional<SWorldData> opData = this.worldData
                .parallelStream()
                .filter(data -> data.spongeWorld().equals(worldData))
                .findAny();
        if (opData.isPresent()) {
            return opData.get();
        }
        //throw new RuntimeException("Need to implement world data loading");
        SWorldData data = new SWorldDataImpl(new SWorldDataBuilder().setWorld(worldData));
        try {
            data.reloadFromConfig();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        this.worldData.add(data);
        return data;
    }
}
