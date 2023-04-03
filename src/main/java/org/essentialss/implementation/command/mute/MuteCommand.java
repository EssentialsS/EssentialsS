package org.essentialss.implementation.command.mute;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.message.MuteType;
import org.essentialss.api.message.adapters.player.command.mute.MutedMessageAdapter;
import org.essentialss.api.message.adapters.player.command.mute.YouAreNowMutedMessageAdapter;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Collection;

public final class MuteCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralUnloadedData> player;
        private final Parameter.Value<MuteType> muteTypes;

        private Execute(Parameter.Value<SGeneralUnloadedData> player, Parameter.Value<MuteType> muteTypes) {
            this.player = player;
            this.muteTypes = muteTypes;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralUnloadedData playerData = context.requireOne(this.player);
            Collection<? extends MuteType> muteTypes = context.all(this.muteTypes);
            MuteType[] muteTypesArray = muteTypes.toArray(new MuteType[0]);
            return MuteCommand.execute(context.cause().audience(), playerData, muteTypesArray);
        }
    }

    private MuteCommand() {
        throw new RuntimeException("Should not run");
    }

    public static Command.Parameterized createMuteCommand() {
        Parameter.Value<SGeneralUnloadedData> playerParameter = SParameters.offlinePlayersNickname(false, d -> true).key("player").build();
        Parameter.Value<MuteType> muteParameter = Parameter.enumValue(MuteType.class).key("type").build();
        Parameter.Multi muteParameters = Parameter.seqBuilder(muteParameter).optional().build();
        return Command.builder().addParameter(playerParameter).addParameter(muteParameters).executor(new Execute(playerParameter, muteParameter)).build();
    }

    public static CommandResult execute(@NotNull Audience audience, @NotNull SGeneralUnloadedData data, @NotNull MuteType... types) {
        MuteType[] usedTypes = types;
        if (0 == types.length) {
            data.setMuted();
            usedTypes = MuteType.values();
        } else {
            data.setMuteTypes(types);
        }
        MutedMessageAdapter mutedAdapter = EssentialsSMain.plugin().messageManager().get().adapters().muted().get();
        Component muteMessage = mutedAdapter.adaptMessage(data, types);
        audience.sendMessage(muteMessage);
        if (data instanceof SGeneralPlayerData) {
            SGeneralPlayerData mutedPlayer = (SGeneralPlayerData) data;
            YouAreNowMutedMessageAdapter nowMutedAdapter = EssentialsSMain.plugin().messageManager().get().adapters().youAreNowMuted().get();
            Component nowMutedMessage = nowMutedAdapter.adaptMessage(usedTypes);
            mutedPlayer.spongePlayer().sendMessage(nowMutedMessage);
        }
        return CommandResult.success();
    }


}
