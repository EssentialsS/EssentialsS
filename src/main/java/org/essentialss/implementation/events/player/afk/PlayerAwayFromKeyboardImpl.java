package org.essentialss.implementation.events.player.afk;

import org.essentialss.api.events.player.afk.PlayerAwayFromKeyboardEvent;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cause;

public class PlayerAwayFromKeyboardImpl implements PlayerAwayFromKeyboardEvent {

    private final Cause cause;
    private final SGeneralPlayerData player;
    private boolean isCancellable;

    public PlayerAwayFromKeyboardImpl(@NotNull SGeneralPlayerData player, @NotNull Cause cause) {
        this.cause = cause;
        this.player = player;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancellable;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancellable = cancel;
    }

    @Override
    public @NotNull SGeneralPlayerData player() {
        return this.player;
    }
}
