package org.essentialss.events.player;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;

import java.util.Optional;

public class BasicPlayerListener {

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @Getter("entity") Player player) {
        SGeneralPlayerData playerData = EssentialsSMain.plugin().playerManager().get().dataFor(player);
        Optional<DamageType> opDamageType = event.context().get(EventContextKeys.DAMAGE_TYPE);
        if (!opDamageType.isPresent()) {
            return;
        }
        DamageType type = opDamageType.get();
        if (playerData.isImmuneTo(type)) {
            event.setCancelled(true);
        }
    }

}
