package org.essentialss.implementation.config.value.simple;

import org.essentialss.api.config.value.SingleConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.LocalDateTime;

public class DateTimeConfigValue implements SingleConfigValue<LocalDateTime> {

    private final @NotNull Object[] nodes;

    public DateTimeConfigValue(Object... nodes) {
        this.nodes = nodes;
    }

    @Override
    public @NotNull Object[] nodes() {
        return this.nodes;
    }

    @Override
    public @NotNull Class<LocalDateTime> type() {
        return LocalDateTime.class;
    }

    @SuppressWarnings("allow-nullable")
    @Override
    public @Nullable LocalDateTime parse(@NotNull ConfigurationNode root) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (node.isNull()) {
            return null;
        }
        int dayOfMonth = node.node("date", "day").getInt();
        int monthOfYear = node.node("date", "month").getInt();
        int year = node.node("date", "year").getInt();

        int hour = node.node("time", "hours").getInt();
        int minutesOfHour = node.node("time", "minutes").getInt();
        int secondsOfMin = node.node("time", "seconds").getInt();
        int nanoOfSecond = node.node("time", "nano").getInt();

        return LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minutesOfHour, secondsOfMin, nanoOfSecond);
    }

    @Override
    public void set(@NotNull ConfigurationNode root, @Nullable LocalDateTime value) throws SerializationException {
        ConfigurationNode node = root.node(this.nodes());
        if (null == value) {
            node.set(null);
            return;
        }

        node.node("date", "day").set(value.getDayOfMonth());
        node.node("date", "month").set(value.getMonthValue());
        node.node("date", "year").set(value.getYear());
        node.node("time", "hours").set(value.getHour());
        node.node("time", "minutes").set(value.getMinute());
        node.node("time", "seconds").set(value.getSecond());
        node.node("time", "nano").set(value.getNano());
    }
}
