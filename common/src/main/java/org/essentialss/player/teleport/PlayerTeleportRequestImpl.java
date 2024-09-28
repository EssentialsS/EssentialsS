package org.essentialss.player.teleport;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.player.teleport.PlayerTeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestBuilder;
import org.essentialss.api.utils.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class PlayerTeleportRequestImpl extends AbstractTeleportRequest implements PlayerTeleportRequest {

    private final @NotNull Component displayName;
    private final @NotNull UUID sender;

    public PlayerTeleportRequestImpl(@NotNull TeleportRequestBuilder builder) {
        super(builder);
        Object sender = new Validator<>(builder.getSender()).notNull().validate();
        if (sender instanceof Player) {
            sender = EssentialsSMain.plugin().playerManager().get().dataFor((Player) sender);
        }
        if (sender instanceof SGeneralUnloadedData) {
            SGeneralUnloadedData playerData = (SGeneralUnloadedData) sender;
            this.displayName = playerData.displayName();
            this.sender = playerData.uuid();
            return;
        }
        throw new RuntimeException("Sender must be either a " + Player.class.getSimpleName() + " or "
                                           + SGeneralUnloadedData.class.getSimpleName());

    }

    @Override
    public @NotNull Component senderFormattedDisplayName() {
        return this.displayName;
    }

    @Override
    public @NotNull UUID sender() {
        return this.sender;
    }
}
