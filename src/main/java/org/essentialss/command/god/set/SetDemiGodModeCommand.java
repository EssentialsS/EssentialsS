package org.essentialss.command.god.set;

import org.essentialss.EssentialsSMain;
import org.essentialss.api.config.configs.GeneralConfig;
import org.essentialss.api.player.data.SGeneralUnloadedData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

import java.util.List;
import java.util.function.Consumer;

public final class SetDemiGodModeCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralUnloadedData> playerParameter;
        private final Consumer<SGeneralUnloadedData> changeImmuneTo;

        private Execute(Parameter.Value<SGeneralUnloadedData> playerParameter, Consumer<SGeneralUnloadedData> changeImmuneTo) {
            this.playerParameter = playerParameter;
            this.changeImmuneTo = changeImmuneTo;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralUnloadedData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            this.changeImmuneTo.accept(player);
            return CommandResult.success();
        }
    }

    private SetDemiGodModeCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createDisableDemiGodModeCommand() {
        return createSetGodModCommand(p -> {
            GeneralConfig config = EssentialsSMain.plugin().configManager().get().general().get();
            List<DamageType> types = config.demiGodImmuneTo().parseDefault(config);
            p.removeImmuneTo(types);
        });
    }

    public static Command.Parameterized createEnableDemiGodModeCommand() {
        return createSetGodModCommand(p -> {
            GeneralConfig config = EssentialsSMain.plugin().configManager().get().general().get();
            List<DamageType> types = config.demiGodImmuneTo().parseDefault(config);
            p.addImmuneTo(types);
        });
    }

    private static Command.Parameterized createSetGodModCommand(Consumer<SGeneralUnloadedData> changeImmunity) {
        Parameter.Value<SGeneralUnloadedData> playerParameter = SParameters
                .offlinePlayersNickname(false, p -> true)
                .key("player")
                .requiredPermission(SPermissions.GOD_MODE_OTHER.node())
                .optional()
                .build();
        return Command
                .builder()
                .permission(SPermissions.GOD_MODE_SELF.node())
                .addParameter(playerParameter)
                .executor(new SetDemiGodModeCommand.Execute(playerParameter, changeImmunity))
                .build();
    }

}
