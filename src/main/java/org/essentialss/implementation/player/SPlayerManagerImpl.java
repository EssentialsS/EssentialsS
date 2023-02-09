package org.essentialss.implementation.player;

import org.essentialss.api.player.SPlayerManager;
import org.essentialss.api.player.data.SGeneralOfflineData;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.implementation.player.data.SPlayerDataImpl;
import org.essentialss.implementation.player.data.SUserDataImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class SPlayerManagerImpl implements SPlayerManager {

    private final Collection<SPlayerDataImpl> activePlayerData = new LinkedHashSet<>();
    private final Collection<SUserDataImpl> activeUserData = new LinkedHashSet<>();

    @Override
    public @NotNull SGeneralPlayerData dataFor(@NotNull Player player) {
        Optional<SPlayerDataImpl> opData = this.activePlayerData
                .parallelStream()
                .filter(data -> data.spongePlayer().equals(player))
                .findAny();
        if (opData.isPresent()) {
            return opData.get();
        }
        SPlayerDataImpl playerData = new SPlayerDataImpl(player);
        this.activePlayerData.add(playerData);
        return playerData;
    }

    @Override
    public @NotNull SGeneralOfflineData dataFor(@NotNull User user) {
        Optional<SUserDataImpl> opData = this.activeUserData
                .parallelStream()
                .filter(data -> data.uuid().equals(user.uniqueId()))
                .findAny();
        if (opData.isPresent()) {
            return opData.get();
        }
        SUserDataImpl playerData = new SUserDataImpl(user);
        this.activeUserData.add(playerData);
        return playerData;
    }
}
