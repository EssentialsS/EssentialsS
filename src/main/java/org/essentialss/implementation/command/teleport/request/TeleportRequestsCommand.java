package org.essentialss.implementation.command.teleport.request;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.essentialss.api.player.data.SGeneralPlayerData;
import org.essentialss.api.player.teleport.TeleportRequest;
import org.essentialss.api.player.teleport.TeleportRequestDirection;
import org.essentialss.api.utils.Constants;
import org.essentialss.api.utils.SParameters;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.friendly.FriendlyStrings;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.misc.CommandPager;
import org.essentialss.implementation.permissions.permission.SPermissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

public final class TeleportRequestsCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> page;
        private final Parameter.Value<SGeneralPlayerData> player;

        private Execute(@NotNull Parameter.Value<SGeneralPlayerData> player, @NotNull Parameter.Value<Integer> page) {
            this.page = page;
            this.player = player;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            int page = context.one(this.page).orElse(1);
            Optional<SGeneralPlayerData> opPlayer = context.one(this.player);
            if (!opPlayer.isPresent()) {
                Subject subject = context.subject();
                if (!(subject instanceof Player)) {
                    return CommandResult.error(Component.text("player needs to be specified"));
                }
                opPlayer = Optional.of(EssentialsSMain.plugin().playerManager().get().dataFor((Player) subject));
            }
            return TeleportRequestsCommand.execute(opPlayer.get(), context.cause().audience(), page);
        }
    }

    private TeleportRequestsCommand() {
        throw new RuntimeException("Should not create");
    }

    public static Command.Parameterized createTeleportRequestsCommand() {
        Parameter.Value<Integer> page = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();
        Parameter.Value<SGeneralPlayerData> player = SParameters
                .onlinePlayer(p -> true)
                .key("player")
                .optional()
                .requiredPermission(SPermissions.VIEW_TELEPORT_REQUESTS_OTHER.node())
                .build();
        return Command.builder().addParameter(page).addParameter(player).executor(new TeleportRequestsCommand.Execute(player, page)).build();
    }

    public static CommandResult execute(@NotNull SGeneralPlayerData playerData, @NotNull Audience audience, int page) {
        UnmodifiableCollection<TeleportRequest> requests = playerData.teleportRequests();
        Collection<TeleportRequest> orderedRequests = new TreeSet<>(Comparator.comparing(TeleportRequest::sentTime));
        orderedRequests.addAll(requests);
        CommandPager.displayList(audience, page, "Teleport Requests", "tpr " + CommandPager.PAGE_ARGUMENT, request -> {
            Component message = Component.empty();
            if (TeleportRequestDirection.TOWARDS_REQUEST_SENDER == request.directionOfTeleport()) {
                message = message.append(Component.text(" - Teleporting to you"));
            } else {
                message = message.append(Component.text(" - Teleporting you to them"));
            }
            Duration ago = Duration.between(request.sentTime(), LocalDateTime.now());
            message = message.append(Component.text(" - Sent " + FriendlyStrings.DURATION.toFriendlyString(ago) + " ago"));
            int redValue = Constants.UNSIGNED_BYTE_MAX;
            Optional<LocalDateTime> opExpireTime = request.expiresAt();
            if (opExpireTime.isPresent()) {
                LocalDateTime sentTime = request.sentTime();
                LocalDateTime expireTime = opExpireTime.get();
                LocalDateTime localTime = LocalDateTime.now();

                message = message.append(Component.text(" - Expires in: " + FriendlyStrings.DURATION.toFriendlyString(Duration.between(localTime, expireTime))));

                Duration totalTimeDuration = Duration.between(sentTime, expireTime);
                long totalTime = totalTimeDuration.toNanos();
                Duration timeLeftDuration = Duration.between(localTime, expireTime);
                long timeLeft = timeLeftDuration.toNanos();

                if (0 == totalTime) {
                    redValue = 0;
                } else {
                    float percentLeft = ((float) timeLeft) / ((float) totalTime);
                    redValue = Math.min(Constants.UNSIGNED_BYTE_MAX, (int) (percentLeft * Constants.UNSIGNED_BYTE_MAX));
                }
            }

            message = message.color(TextColor.color(Constants.UNSIGNED_BYTE_MAX, redValue, redValue));

            message = request.senderFormattedDisplayName().append(message);
            return message;
        }, orderedRequests);

        return CommandResult.success();
    }
}
