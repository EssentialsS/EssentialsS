package org.essentialss.implementation.player.data;

import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurateException;

import java.util.UUID;

public class SProfileDataImpl extends AbstractProfileData implements SGeneralUnloadedData {

    private final GameProfile profile;

    public SProfileDataImpl(@NotNull GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void reloadFromConfig() throws ConfigurateException {
        UserDataSerializer.load(this);
    }

    @Override
    public void saveToConfig() throws ConfigurateException {
        UserDataSerializer.save(this);
    }

    @Override
    public String playerName() {
        return this.profile.name().orElseGet(this.profile::examinableName);
    }

    @Override
    public @NotNull UUID uuid() {
        return this.profile.uuid();
    }
}
