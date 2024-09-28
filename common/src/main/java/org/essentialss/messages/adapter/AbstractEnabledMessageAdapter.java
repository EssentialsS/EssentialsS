package org.essentialss.messages.adapter;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.adapters.MessageAdapter;
import org.essentialss.config.value.primitive.BooleanConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractEnabledMessageAdapter extends AbstractMessageAdapter implements MessageAdapter.Enabled {

    private final boolean isEnabledByDefault;

    protected AbstractEnabledMessageAdapter(boolean isEnabledByDefault, SingleConfigValue.Default<Component> configValue) {
        super(configValue);
        this.isEnabledByDefault = isEnabledByDefault;
    }

    @Override
    public @NotNull SingleConfigValue.Default<Boolean> enabledValue() {
        Object[] nodes = Arrays.copyOf(this.configValue().nodes(), this.configValue().nodes().length);
        nodes[nodes.length - 1] = "Enabled";
        return new BooleanConfigValue(this.isEnabledByDefault, nodes);
    }
}
