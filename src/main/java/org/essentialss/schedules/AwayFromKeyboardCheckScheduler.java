package org.essentialss.schedules;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.AwayFromKeyboardConfig;
import org.essentialss.api.events.player.afk.PlayerKickedForIdlingEvent;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardBarMessageAdapter;
import org.essentialss.api.message.adapters.player.listener.afk.AwayFromKeyboardForTooLongMessageAdapter;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.events.player.afk.PlayerAwayFromKeyboardImpl;
import org.essentialss.events.player.afk.PlayerKickedForIdlingImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class AwayFromKeyboardCheckScheduler implements Runnable {

    private void kickBar(@NotNull Collection<SGeneralPlayerData> players, AwayFromKeyboardConfig config) {
        Duration duration;
        try {
            duration = config.durationUntilKick().parse(config);
        } catch (SerializationException e) {
            return;
        }
        if (null == duration) {
            return;
        }
        players.stream().filter(SGeneralPlayerData::isShowingAwayFromKeyboard).forEach(p -> {
            LocalDateTime lastAction = p.lastPlayerAction();
            Duration current = Duration.between(lastAction, LocalDateTime.now());
            Duration left = duration.minus(current);
            AwayFromKeyboardBarMessageAdapter adapter = EssentialsSMain.plugin().messageManager().get().adapters().awayFromKeyboardBar().get();
            if (!adapter.isEnabled()) {
                return;
            }

            float percent = Math.min(((float) current.toNanos()) / duration.toNanos(), 1.0f);
            Optional<BossBar> opBar = p.barUntilKick();
            if (!opBar.isPresent()) {
                BossBar bar = BossBar.bossBar(adapter.adaptMessage(left), percent, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
                opBar = Optional.of(bar);
                p.setBarUntilKick(bar);
            }
            BossBar bossBar = opBar.get().progress(percent).name(adapter.adaptMessage(left));
            p.spongePlayer().showBossBar(bossBar);
        });
    }

    private void kickStatus(@NotNull Collection<SGeneralPlayerData> players, AwayFromKeyboardConfig config) {
        Duration duration;
        try {
            duration = config.durationUntilKick().parse(config);
        } catch (SerializationException e) {
            return;
        }
        if (null == duration) {
            return;
        }
        AwayFromKeyboardForTooLongMessageAdapter messageAdapter = EssentialsSMain.plugin().messageManager().get().adapters().awayFromKeyboardForTooLong().get();
        players
                .stream()
                .filter(SGeneralPlayerData::isShowingAwayFromKeyboard)
                .filter(p -> LocalDateTime.now().isAfter(p.lastPlayerAction().plus(duration)))
                .filter(p -> p.spongePlayer() instanceof ServerPlayer)
                .forEach(p -> {
                    Component banMessage = messageAdapter.isEnabled() ? messageAdapter.adaptMessage(p) : null;
                    Cause cause = Cause.of(EventContext
                                                   .builder()
                                                   .add(EventContextKeys.PLUGIN, EssentialsSMain.plugin().container())
                                                   .add(EventContextKeys.PLAYER, p.spongePlayer())
                                                   .build(), p);
                    PlayerKickedForIdlingEvent event = new PlayerKickedForIdlingImpl(banMessage, p, cause);
                    if (event.isCancelled()) {
                        return;
                    }
                    event.kickAdaptedMessage().ifPresent(c -> {
                        ((ServerPlayer) p.spongePlayer()).kick();
                    });
                });
    }

    @Override
    public void run() {
        UnmodifiableCollection<SGeneralPlayerData> players = EssentialsSMain.plugin().playerManager().get().allPlayerData();
        AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
        this.statusState(players, config);
        this.kickStatus(players, config);
        this.kickBar(players, config);
    }

    private void statusState(@NotNull Collection<SGeneralPlayerData> players, AwayFromKeyboardConfig config) {
        Duration duration;
        try {
            duration = config.durationUntilStatus().parse(config);
        } catch (SerializationException e) {
            return;
        }
        if (null == duration) {
            return;
        }

        players.stream().filter(SGeneralPlayerData::isShowingAwayFromKeyboard).filter(p -> !p.hasShownAwayFromKeyboardMessage()).forEach(p -> {
            Cause cause = Cause.of(EventContext
                                           .builder()
                                           .add(EventContextKeys.PLUGIN, EssentialsSMain.plugin().container())
                                           .add(EventContextKeys.PLAYER, p.spongePlayer())
                                           .build(), p);
            Cancellable event = new PlayerAwayFromKeyboardImpl(p, cause);
            if (event.isCancelled()) {
                return;
            }
            p.setAwayFromKeyboard();
        });
    }

    public static Task createTask() {
        AwayFromKeyboardConfig config = EssentialsSMain.plugin().configManager().get().awayFromKeyboard().get();
        Duration duration;
        try {
            duration = config.durationUntilStatus().parse(config);
        } catch (SerializationException e) {
            throw new IllegalStateException(
                    "Could not read " + Arrays.stream(config.durationUntilStatus().nodes()).map(Object::toString).collect(Collectors.joining(" -> ")), e);
        }
        if (null == duration) {
            throw new IllegalStateException(
                    "Could not read " + Arrays.stream(config.durationUntilStatus().nodes()).map(Object::toString).collect(Collectors.joining(" -> ")));
        }

        if (duration.toNanos() > Duration.ofSeconds(1).toNanos()) {
            duration = Duration.ofSeconds(1);
        }

        return Task.builder().plugin(EssentialsSMain.plugin().container()).execute(new AwayFromKeyboardCheckScheduler()).interval(duration).build();
    }
}
