package org.essentialss.config.value;

import org.essentialss.api.config.value.CollectionConfigValue;
import org.essentialss.api.config.value.ConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class ListDefaultConfigValueImpl<T> extends ListConfigValueImpl<T> implements CollectionConfigValue.Default<T> {

    private final Supplier<List<T>> defaultValue;

    public ListDefaultConfigValueImpl(@NotNull ConfigValue<T> parse, Supplier<List<T>> defaultValue, @NotNull Object... nodes) {
        super(parse, nodes);
        this.defaultValue = defaultValue;
    }

    @Override
    public List<T> defaultValue() {
        return this.defaultValue.get();
    }
}
