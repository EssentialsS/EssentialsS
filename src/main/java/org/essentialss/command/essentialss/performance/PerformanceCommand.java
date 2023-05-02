package org.essentialss.command.essentialss.performance;

import com.sun.management.OperatingSystemMXBean;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.essentialss.api.utils.Constants;
import org.essentialss.misc.CommandPager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class PerformanceCommand {

    private static final class Execute implements CommandExecutor {

        private final Parameter.Value<Integer> pageParameter;

        private Execute(Parameter.Value<Integer> pageParameter) {
            this.pageParameter = pageParameter;
        }

        @Override
        public CommandResult execute(CommandContext context) throws CommandException {
            int page = context.one(this.pageParameter).orElse(1);
            return PerformanceCommand.execute(context.cause().audience(), page);
        }
    }

    private static Component createPercentageText(double percent, boolean inverse) {
        TextColor colour = percentToColour(percent, inverse);
        return Component.text(Math.round(percent * 100) + "%").color(colour);
    }

    public static Command.Parameterized createPerformanceCommand() {
        Parameter.Value<Integer> page = Parameter.rangedInteger(1, Integer.MAX_VALUE).key("page").optional().build();

        return Command.builder().executor(new Execute(page)).addParameter(page).build();
    }

    public static CommandResult execute(Audience audience, int page) {
        Runtime runtime = Runtime.getRuntime();
        Collection<Component> text = new ArrayList<>();

        if (Sponge.isServerAvailable()) {
            double currentTicks = Sponge.server().ticksPerSecond();
            double averageTickTime = Sponge.server().averageTickTime();

            Component currentTPSText = Component.text("Current TPS: ").append(Component.text(currentTicks).color(percentToColour(currentTicks / 20.0, false)));
            Component averageTPSText = Component
                    .text("Average TPS (milliseconds): ")
                    .append(Component.text(averageTickTime).color((5.5 < averageTickTime) ? NamedTextColor.RED : NamedTextColor.GREEN));

            text.add(currentTPSText);
            text.add(averageTPSText);
        }

        OperatingSystemMXBean osPlatform = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double cpuUsage = osPlatform.getProcessCpuLoad();
        int threadCount = runtime.availableProcessors();
        Component cpuText = Component.text("threads: " + threadCount + " - CPU: ").append(createPercentageText(cpuUsage, true));
        text.add(cpuText);

        long currentMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        double memoryPercent = (double) currentMemory / maxMemory;
        Component ramUsageText = Component.text("RAM: ").append(createPercentageText(memoryPercent, false));
        text.add(ramUsageText);

        Set<String> has = new TreeSet<>();
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                if (store.isReadOnly()) {
                    continue;
                }
                long freeSpace = store.getUnallocatedSpace();
                long totalSpace = store.getTotalSpace();
                if (totalSpace == 0) {
                    continue;
                }
                double percent = (double) (totalSpace - freeSpace) / totalSpace;

                String nameType = store.name() + " - " + store.type() + " - ";
                if (has.contains(nameType)) {
                    continue;
                }

                Component driveText = Component.text(nameType).append(createPercentageText(percent, true));
                text.add(driveText);
                has.add(nameType);
            } catch (IOException e) {
                continue;
            }
        }

        CommandPager.displayList(audience, page, "Performance", "essentialss performance", (t) -> t, text);
        return CommandResult.success();
    }

    private static TextColor percentToColour(double percent, boolean inverse) {
        double colourValue = percent * Constants.UNSIGNED_BYTE_MAX;
        int colour = (int) Math.min(Constants.UNSIGNED_BYTE_MAX, Math.round(colourValue));
        int green = inverse ? (Constants.UNSIGNED_BYTE_MAX - colour) : colour;
        int red = inverse ? colour : (Constants.UNSIGNED_BYTE_MAX - colour);

        return TextColor.color(red, green, 0);
    }
}
