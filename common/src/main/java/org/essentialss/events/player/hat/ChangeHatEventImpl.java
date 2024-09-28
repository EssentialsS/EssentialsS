package org.essentialss.events.player.hat;

import org.essentialss.api.events.player.hat.ChangeHatEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.ItemStack;

public class ChangeHatEventImpl implements ChangeHatEvent {

    private final @NotNull Player player;
    private final @NotNull Cause cause;
    private final @NotNull Transaction<ItemStack> itemStack;
    private boolean isCancelled;

    public ChangeHatEventImpl(@NotNull Player player, @NotNull Transaction<ItemStack> itemStack, @NotNull Cause cause) {
        this.itemStack = itemStack;
        this.cause = cause;
        this.player = player;
    }

    @Override
    public @NotNull Player spongePlayer() {
        return this.player;
    }

    @Override
    public @NotNull Transaction<ItemStack> item() {
        return this.itemStack;
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
    public Cause cause() {
        return this.cause;
    }
}
