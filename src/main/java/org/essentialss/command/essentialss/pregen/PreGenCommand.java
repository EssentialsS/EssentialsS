package org.essentialss.command.essentialss.pregen;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.world.SPreGenData;
import org.essentialss.api.world.SWorldData;
import org.essentialss.misc.CommandHelper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;

public class PreGenCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SWorldData> worldDataParameter;
        private final Parameter.Value<Vector3i> centerChunkParameter;
        private final Parameter.Value<Integer> radiusParameter;

        private Execute(Parameter.Value<SWorldData> worldDataParameter,
                        Parameter.Value<Vector3i> centerChunkParameter,
                        Parameter.Value<Integer> radiusParameter) {
            this.worldDataParameter = worldDataParameter;
            this.radiusParameter = radiusParameter;
            this.centerChunkParameter = centerChunkParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SWorldData worldData = CommandHelper.worldDataOrTarget(context, this.worldDataParameter);
            Vector3i centerChunk = CommandHelper.chunkOrTarget(context, this.centerChunkParameter);
            int radius = context.requireOne(this.radiusParameter);
            return PreGenCommand.execute(context.cause().audience(), worldData, centerChunk, radius);
        }
    }

    public static Command.Parameterized createPreGenCommand() {
        Parameter.Value<SWorldData> worldDataParameter = SParameters.worldData().key("world").optional().build();
        Parameter.Value<Vector3i> chunkParameter = SParameters.vector3Integer().key("chunk").optional().build();
        Parameter.Value<Integer> radiusParameter = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("radius").build();

        return Command
                .builder()
                .addParameter(worldDataParameter)
                .addParameter(chunkParameter)
                .addParameter(radiusParameter)
                .executor(new Execute(worldDataParameter, chunkParameter, radiusParameter))
                .build();
    }

    public static CommandResult execute(Audience audience, SWorldData world, Vector3i centerChunk, int radius) {
        try {
            Optional<SPreGenData> opPreGen = world.setPreGeneratingData(centerChunk, radius, audience);
            if (opPreGen.isPresent()) {
                return CommandResult.success();
            }
        } catch (IllegalStateException e) {
            return CommandResult.error(Component.text(e.getMessage()));
        }
        return CommandResult.error(Component.text("Already generating data"));
    }
}
