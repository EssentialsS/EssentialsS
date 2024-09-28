package org.essentialss.schedules;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CooldownScheduler implements Runnable {

    private void checkRemove(SGeneralPlayerData player,
                             Function<SGeneralPlayerData, Optional<LocalDateTime>> toCooldown,
                             Consumer<SGeneralPlayerData> toRemove) {
        Optional<LocalDateTime> opTime = toCooldown.apply(player);
        if (!opTime.isPresent()) {
            return;
        }
        LocalDateTime time = opTime.get();
        if (!time.isAfter(LocalDateTime.now())) {
            toRemove.accept(player);
        }
    }

    @Override
    public void run() {
        EssentialsSMain
                .plugin()
                .playerManager()
                .get()
                .allPlayerData()
                .forEach(player -> this.checkRemove(player, SGeneralPlayerData::kitCooldownRelease, SGeneralPlayerData::removeKitCooldown));
    }

    public static Task createKitCooldownTask() {
        return Task.builder().plugin(EssentialsSMain.plugin().container()).execute(new CooldownScheduler()).interval(Ticks.of(1)).build();
    }
}
