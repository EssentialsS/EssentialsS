package org.essentialss.events.player.afk;

import org.essentialss.api.events.player.afk.PlayerBackFromKeyboardEvent;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Cause;

public class PlayerBackFromKeyboardImpl implements PlayerBackFromKeyboardEvent {

    private final Cause cause;
    private final SGeneralPlayerData player;
    private boolean isCancelled;

    public PlayerBackFromKeyboardImpl(@NotNull SGeneralPlayerData player, @NotNull Cause cause) {
        this.cause = cause;
        this.player = player;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull SGeneralPlayerData player() {
        return this.player;
    }
}
