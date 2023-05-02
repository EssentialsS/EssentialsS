package org.essentialss.command.nick;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

public final class WhoIsCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralUnloadedData> value;

        private Execute(@NotNull Parameter.Value<SGeneralUnloadedData> value) {
            this.value = value;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralUnloadedData player = context.requireOne(this.value);
            return WhoIsCommand.execute(player, context.cause().audience());
        }
    }

    private WhoIsCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createWhoIsCommand() {
        Parameter.Value<SGeneralUnloadedData> player = SParameters.offlinePlayersNickname(true, t -> true).key("nickname").build();
        return Command.builder().addParameter(player).executor(new Execute(player)).build();
    }

    public static CommandResult execute(@NotNull SGeneralUnloadedData data, @NotNull Audience audience) {
        Component message = EssentialsSMain.plugin().messageManager().get().adapters().whoIs().get().adaptMessage(data);
        audience.sendMessage(message);
        return CommandResult.success();
    }

}
