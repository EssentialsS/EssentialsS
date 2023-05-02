package org.essentialss.events.player.afk;

import net.kyori.adventure.text.Component;
import org.essentialss.api.events.player.afk.PlayerKickedForIdlingEvent;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.event.Cause;

public class PlayerKickedForIdlingImpl implements PlayerKickedForIdlingEvent {

    private final Cause cause;
    private final SGeneralPlayerData player;
    private final Component originalMessage;
    private boolean isCancelled;
    private @Nullable Component newMessage;

    public PlayerKickedForIdlingImpl(@NotNull Component kickMessage, @NotNull SGeneralPlayerData player, @NotNull Cause cause) {
        this.cause = cause;
        this.player = player;
        this.originalMessage = kickMessage;
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
    public @NotNull Component kickMessage() {
        if (null == this.newMessage) {
            return this.originalMessage;
        }
        return this.newMessage;
    }

    @Override
    public @NotNull Component originalKickMessage() {
        return this.originalMessage;
    }

    @Override
    public @NotNull SGeneralPlayerData player() {
        return this.player;
    }

    @Override
    public void setKickMessage(@NotNull Component kickMessage) {
        this.newMessage = kickMessage;
    }
}
