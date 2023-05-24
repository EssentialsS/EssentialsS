package org.essentialss.command.vanish;

import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.VanishState;

import java.util.Optional;
import java.util.function.Function;

public final class VanishCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<SGeneralPlayerData> playerParameter;
        private final Parameter.Value<Boolean> affectsMonsterSpawningParameter;
        private final Parameter.Value<Boolean> soundsParameter;
        private final Parameter.Value<Boolean> particleParameter;
        private final Parameter.Value<Boolean> collisionParameter;
        private final Parameter.Value<Boolean> targetParameter;


        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter,
                        Parameter.Value<Boolean> affectsMonsterSpawningParameter,
                        Parameter.Value<Boolean> soundsParameter,
                        Parameter.Value<Boolean> particleParameter,
                        Parameter.Value<Boolean> collisionParameter,
                        Parameter.Value<Boolean> targetParameter) {
            this.affectsMonsterSpawningParameter = affectsMonsterSpawningParameter;
            this.soundsParameter = soundsParameter;
            this.playerParameter = playerParameter;
            this.particleParameter = particleParameter;
            this.collisionParameter = collisionParameter;
            this.targetParameter = targetParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            Optional<Boolean> opAffectsMonsterSpawns = context.one(this.affectsMonsterSpawningParameter);
            Optional<Boolean> opSound = context.one(this.soundsParameter);
            Optional<Boolean> opParticle = context.one(this.particleParameter);
            Optional<Boolean> opCollision = context.one(this.collisionParameter);
            Optional<Boolean> opTarget = context.one(this.targetParameter);
            return VanishCommand.execute(player, state -> {
                if (opAffectsMonsterSpawns.isPresent()) {
                    state = state.affectMonsterSpawning(opAffectsMonsterSpawns.get());
                }
                if (opSound.isPresent()) {
                    state = state.createSounds(opSound.get());
                }
                if (opParticle.isPresent()) {
                    state = state.createParticles(opParticle.get());
                }
                if (opCollision.isPresent()) {
                    state = state.ignoreCollisions(opCollision.get());
                }
                if (opTarget.isPresent()) {
                    return state.untargetable(opTarget.get());
                }
                return state;
            });
        }
    }

    private VanishCommand() {
        throw new RuntimeException("Should not generate");
    }

    public static Command.Parameterized createVanishCommand() {
        Parameter.Value<SGeneralPlayerData> playerParameter = SParameters.onlinePlayer(p -> true).key("player").optional().build();
        Parameter.Value<Boolean> affectsMonsterSpawning = Parameter.bool().key("affectsMonsterSpawning").optional().build();
        Parameter.Value<Boolean> playSound = Parameter.bool().key("playSound").optional().build();
        Parameter.Value<Boolean> showParticle = Parameter.bool().key("showParticles").optional().build();
        Parameter.Value<Boolean> ignoreCollision = Parameter.bool().key("ignoreCollision").optional().build();
        Parameter.Value<Boolean> noTarget = Parameter.bool().key("noTarget").optional().build();

        Flag affectsMonsterSpawningFlag = Flag
                .builder()
                .aliases("affectsMonsterSpawning", "monsterSpawning", "ms")
                .setParameter(affectsMonsterSpawning)
                .build();
        Flag playSoundFlag = Flag.builder().aliases("playSound", "sound", "s").setParameter(playSound).build();
        Flag showParticleFlag = Flag.builder().aliases("showParticles", "particles", "p").setParameter(showParticle).build();
        Flag ignoreCollisionFlag = Flag.builder().aliases("ignoreCollision", "noCollision", "nc").setParameter(ignoreCollision).build();
        Flag noTargetFlag = Flag.builder().aliases("ignoreTarget", "noTarget", "nt").setParameter(noTarget).build();

        return Command
                .builder()
                .addParameter(playerParameter)
                .addFlag(affectsMonsterSpawningFlag)
                .addFlag(playSoundFlag)
                .addFlag(showParticleFlag)
                .addFlag(ignoreCollisionFlag)
                .addFlag(noTargetFlag)
                .executor(new Execute(playerParameter, affectsMonsterSpawning, playSound, showParticle, ignoreCollision, noTarget))
                .build();
    }

    public static CommandResult execute(SGeneralPlayerData player, Function<VanishState, VanishState> state) {
        VanishState vanish = state.apply(VanishState.vanished());

        player.spongePlayer().offer(Keys.VANISH_STATE, vanish);
        return CommandResult.success();
    }
}
