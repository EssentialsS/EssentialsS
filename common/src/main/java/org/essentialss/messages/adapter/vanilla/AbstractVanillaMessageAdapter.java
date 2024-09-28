package org.essentialss.messages.adapter.vanilla;

import net.kyori.adventure.text.Component;
import org.essentialss.api.config.value.SingleConfigValue;
import org.essentialss.api.message.adapters.vanilla.VanillaMessageAdapter;
import org.essentialss.config.value.primitive.BooleanConfigValue;
import org.essentialss.messages.adapter.AbstractEnabledMessageAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractVanillaMessageAdapter extends AbstractEnabledMessageAdapter implements VanillaMessageAdapter {

    protected AbstractVanillaMessageAdapter(boolean isEnabledByDefault, SingleConfigValue.Default<Component> configValue) {
        super(isEnabledByDefault, configValue);
    }

    @Override
    public @NotNull SingleConfigValue.Default<Boolean> useVanilla() {
        Object[] nodes = Arrays.copyOf(this.configValue().nodes(), this.configValue().nodes().length);
        nodes[nodes.length - 1] = "UseVanillaMessage";
        return new BooleanConfigValue(true, nodes);
    }
}
