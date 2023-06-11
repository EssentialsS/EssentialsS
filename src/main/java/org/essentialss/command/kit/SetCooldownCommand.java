package org.essentialss.command.kit;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.group.Group;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.utils.SParameters;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.configurate.ConfigurateException;

import java.time.Duration;

public final class SetCooldownCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Duration> durationParameter;
        private final Parameter.Value<Group> groupParameter;
        private final Parameter.Value<Kit> kitParameter;

        public Execute(Parameter.Value<Kit> kit, Parameter.Value<Group> group, Parameter.Value<Duration> duration) {
            this.kitParameter = kit;
            this.groupParameter = group;
            this.durationParameter = duration;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            Duration duration = context.requireOne(this.durationParameter);
            Kit kit = context.requireOne(this.kitParameter);
            Group group = context.requireOne(this.groupParameter);
            return SetCooldownCommand.execute(kit, group, duration);
        }
    }

    private SetCooldownCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createCooldownCommand() {
        Parameter.Value<Duration> durationParameter = Parameter.duration().key("duration").build();
        Parameter.Value<Group> groupParameter = SParameters.group().key("group").build();
        Parameter.Value<Kit> kitParameter = SParameters.kitParameter((context, kit) -> true).key("kit").build();
        return Command
                .builder()
                .addParameter(kitParameter)
                .addParameter(groupParameter)
                .addParameter(durationParameter)
                .executor(new Execute(kitParameter, groupParameter, durationParameter))
                .build();
    }

    public static CommandResult execute(Kit kit, Group group, Duration cooldown) {
        kit.setCooldown(group, cooldown);
        try {
            EssentialsSMain.plugin().kitManager().get().save(kit);
        } catch (ConfigurateException e) {
            return CommandResult.error(Component.text(e.getMessage()));
        }
        return CommandResult.success();
    }
}
