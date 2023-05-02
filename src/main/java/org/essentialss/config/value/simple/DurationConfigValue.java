package org.essentialss.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;

public class DurationConfigValue implements SingleConfigValue<Duration> {

    private final @NotNull Object[] nodes;

    public DurationConfigValue(Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @Override
    public @NotNull Class<Duration> type() {
        return Duration.class;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable Duration parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (node.isNull()) {
            return null;
        }
        int dayOfMonth = node.node("date", "day").getInt();
        Duration durationDay = Duration.ofDays(dayOfMonth);

        int hour = node.node("time", "hours").getInt();
        Duration durationHour = Duration.ofHours(hour);
        int minutesOfHour = node.node("time", "minutes").getInt();
        Duration durationMinute = Duration.ofMinutes(minutesOfHour);
        int secondsOfMin = node.node("time", "seconds").getInt();
        Duration durationSeconds = Duration.ofSeconds(secondsOfMin);
        int nanoOfSecond = node.node("time", "nano").getInt();
        Duration durationNano = Duration.ofNanos(nanoOfSecond);

        return durationDay.plus(durationHour).plus(durationMinute).plus(durationSeconds).plus(durationNano);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable Duration value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }
        node.node("date", "day").set(value.toDays());
        value = value.minusDays(value.toDays());
        node.node("time", "hours").set(value.toHours());
        value = value.minusHours(value.toHours());
        node.node("time", "minutes").set(value.toMinutes());
        value = value.minusMinutes(value.toMinutes());
        node.node("time", "seconds").set(value.getSeconds());
        value = value.minusSeconds(value.getSeconds());
        node.node("time", "nano").set(value.getNano());
    }
}
