package org.essentialss.player;

import net.kyori.adventure.identity.Identity;
import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.data.module.load.ModuleLoader;
import org.essentialss.api.utils.arrays.impl.SingleUnmodifiableCollection;
import org.essentialss.player.data.AbstractProfileData;
import org.essentialss.player.data.SPlayerDataImpl;
import org.essentialss.player.data.SProfileDataImpl;
import org.essentialss.player.data.SUserDataImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;

public class SPlayerManagerImpl implements SPlayerManager {

    private final Collection<AbstractProfileData> data = new LinkedHashSet<>();
    private final Collection<ModuleLoader<?, ?>> moduleLoaders = new LinkedTransferQueue<>();

    @Override
    public @NotNull SGeneralOfflineData dataFor(@NotNull User user) {
        Optional<ServerPlayer> opPlayer = user.player();
        if (opPlayer.isPresent()) {
            return this.dataFor(opPlayer.get());
        }
        Optional<AbstractProfileData> opData = this.dataForProfile(user.profile());
        if (opData.isPresent() && (opData.get() instanceof SGeneralOfflineData)) {
            return (SGeneralOfflineData) opData.get();
        }

        SUserDataImpl playerData = new SUserDataImpl(user);
        try {
            playerData.reloadFromConfig();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        if (opData.isPresent()) {
            playerData.applyChangesFrom(opData.get());
            this.data.remove(opData.get());
        }
        this.data.add(playerData);
        return playerData;
    }

    @Override
    public @NotNull SGeneralUnloadedData dataFor(@NotNull GameProfile profile) {
        if (Sponge.isServerAvailable()) {
            Optional<ServerPlayer> opPlayer = Sponge.server().onlinePlayers().stream().filter(player -> player.profile().equals(profile)).findAny();
            if (opPlayer.isPresent()) {
                return this.dataFor(opPlayer.get());
            }
        }

        Optional<AbstractProfileData> opData = this.dataForProfile(profile);
        if (opData.isPresent()) {
            return opData.get();
        }
        SProfileDataImpl playerData = new SProfileDataImpl(profile);
        try {
            playerData.reloadFromConfig();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
        this.data.add(playerData);
        return playerData;
    }

    @Override
    public @NotNull SGeneralPlayerData dataFor(@NotNull Player player) {
        Optional<AbstractProfileData> opData = this.dataForProfile(player.profile());
        if (opData.isPresent() && (opData.get() instanceof SGeneralPlayerData)) {
            return (SGeneralPlayerData) opData.get();
        }

        SPlayerDataImpl playerData = new SPlayerDataImpl(player);
        try {
            playerData.reloadFromConfig();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        if (opData.isPresent()) {
            playerData.applyChangesFrom(opData.get());
            this.data.remove(opData.get());
        }
        this.data.add(playerData);
        return playerData;
    }

    @Override
    public @NotNull Collection<ModuleLoader<?, ?>> dataLoaders() {
        return new SingleUnmodifiableCollection<>(this.moduleLoaders);
    }

    @Override
    public void register(@NotNull ModuleLoader<?, ?> loader) {
        this.moduleLoaders.add(loader);
    }

    private @NotNull Optional<AbstractProfileData> dataForProfile(@NotNull Identity profile) {
        return this.data.stream().filter(data -> data.uuid().equals(profile.uuid())).findAny();
    }

    public void unload(@NotNull AbstractProfileData data) {
        this.data.remove(data);
    }
}
