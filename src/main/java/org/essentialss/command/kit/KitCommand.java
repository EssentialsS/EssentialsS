package org.essentialss.command.kit;

import net.kyori.adventure.text.Component;
import org.essentialss.EssentialsSMain;
import org.essentialss.api.group.Group;
import org.essentialss.api.kit.Kit;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.utils.SParameters;
import org.essentialss.misc.CommandHelper;
import org.essentialss.permissions.permission.SPermissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.service.permission.Subject;

import java.time.Duration;
import java.time.LocalDateTime;

public final class KitCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Kit> kitParameter;
        private final Parameter.Value<SGeneralPlayerData> playerParameter;
        private final boolean equip;

        private Execute(Parameter.Value<SGeneralPlayerData> playerParameter, Parameter.Value<Kit> kitParameter, boolean equip) {
            this.kitParameter = kitParameter;
            this.playerParameter = playerParameter;
            this.equip = equip;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            SGeneralPlayerData player = CommandHelper.playerDataOrTarget(context, this.playerParameter);
            Kit kit = context.requireOne(this.kitParameter);
            return KitCommand.execute(context.subject(), player, kit, this.equip);
        }
    }

    private KitCommand() {
        throw new RuntimeException("Should not generate");
    }

    private static Command.Builder createGenericCommand(boolean equip) {
        Parameter.Value<SGeneralPlayerData> player = SParameters
                .onlinePlayer(p -> true)
                .key("player")
                .requiredPermission(SPermissions.KIT_OTHER.node())
                .optional()
                .build();
        Parameter.Value<Kit> kit = SParameters
                .kitParameter((context, thisKit) -> context.hasPermission(
                        SPermissions.ABSTRACT_KIT_TYPE.node() + thisKit.plugin().metadata().id() + "." + thisKit.name()))
                .key("kit")
                .build();
        return Command
                .builder()
                .executor(new Execute(player, kit, equip))
                .addParameter(kit)
                .addParameter(player)
                .executionRequirements(cause -> Sponge.isServerAvailable());
    }

    public static Command.Parameterized createKitCommand() {
        return createGenericCommand(true)
                .addChild(AddKitCommand.createAddKitCommand(), "add", "register")
                .addChild(RemoveKitCommand.createRemoveKitCommand(), "remove")
                .addChild(SetCooldownCommand.createCooldownCommand(), "cooldown")
                .addChild(createGenericCommand(true).build(), "equip")
                .addChild(createGenericCommand(false).build(), "open")
                .build();
    }


    public static CommandResult execute(Subject subject, SGeneralPlayerData player, Kit kit, boolean equip) {
        if (player.kitCooldownRelease().isPresent()) {
            //display message
            return CommandResult.error(Component.text("Kits are on cooldown"));
        }

        if (equip) {
            Group group = EssentialsSMain.plugin().groupManager().get().group(subject);
            Duration cooldownDuration = group.kitCooldownDefault(kit);
            player.setKitCooldownRelease(LocalDateTime.now().plus(cooldownDuration));
            kit.apply(player.spongePlayer().inventory());
            return CommandResult.success();
        }
        if (!(player.spongePlayer() instanceof ServerPlayer)) {
            throw new RuntimeException("Ran a server only command on client");
        }
        ServerPlayer sPlayer = (ServerPlayer) player.spongePlayer();


        InventoryMenu menu = kit.createInventory().asMenu();
        menu.setTitle(Component.text(kit.displayName()));
        menu.open(sPlayer);

        Group group = EssentialsSMain.plugin().groupManager().get().group(subject);
        Duration cooldownDuration = group.kitCooldownDefault(kit);
        player.setKitCooldownRelease(LocalDateTime.now().plus(cooldownDuration));
        return CommandResult.success();
    }

}
