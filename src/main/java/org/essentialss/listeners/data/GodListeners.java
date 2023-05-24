package org.essentialss.listeners.data;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;

import java.util.Optional;

public class GodListeners {

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @Getter("entity") Player player, @First DamageSource source) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
        if (!playerData.isImmuneTo(source.type())) {
            return;
        }
        event.setCancelled(true);
    }

    @Listener
    public void onPlayerFoodChange(ChangeDataHolderEvent.ValueChange event, @Getter("targetHolder") Player player) {
        DataTransactionResult result = event.originalChanges();
        Optional<Value.Immutable<Integer>> opData = result.successfulValue(Keys.FOOD_LEVEL);
        if (!opData.isPresent()) {
            return;
        }
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
        if (!playerData.unlimitedFood()) {
            return;
        }
        event.setCancelled(true);
    }

}
