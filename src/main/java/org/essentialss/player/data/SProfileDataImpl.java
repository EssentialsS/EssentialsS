package org.essentialss.player.data;

import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.UUID;

public class SProfileDataImpl extends AbstractProfileData implements SGeneralUnloadedData {

    private final GameProfile profile;

    public SProfileDataImpl(@NotNull GameProfile profile){
        this.profile = profile;
    }

    @NotNull
    @Override
    public String playerName() {
        return this.profile.name().orElseGet(this.profile::examinableName);
    }

    @NotNull
    @Override
    public UUID uuid() {
        return this.profile.uuid();
    }

    @Override
    public void reloadFromConfig() throws ConfigurateException, SerializationException {
        UserDataSerializer.load(this);
    }

    @Override
    public void saveToConfig() throws ConfigurateException, SerializationException {
        UserDataSerializer.save(this);

    }
}
