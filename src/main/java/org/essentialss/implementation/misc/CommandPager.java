package org.essentialss.implementation.misc;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.essentialss.api.utils.validation.ValidationRules;
import org.essentialss.api.utils.validation.Validator;
import org.essentialss.implementation.EssentialsSMain;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class CommandPager<T> {

    private final int pageSize;
    private final List<T> originalList;

    public static final int MINIMUM_PAGE_SIZE = 1;
    public static final int MINIMUM_PAGE = 1;

    public CommandPager(Collection<T> originalList) {
        this(EssentialsSMain
                     .plugin()
                     .configManager()
                     .get()
                     .general()
                     .get()
                     .pageSize()
                     .parseDefault(EssentialsSMain.plugin().configManager().get().general().get()), originalList);
    }

    public CommandPager(int pageSize, Collection<T> originalList) {
        new Validator<>(pageSize).rule(ValidationRules.isGreaterThan(MINIMUM_PAGE_SIZE - 1)).validate();
        this.originalList = new LinkedList<>(originalList);
        this.pageSize = pageSize;
    }

    public Collection<T> originalList() {
        return this.originalList;
    }

    public int totalPages() {
        return Math.max((this.originalList.size() / this.pageSize), MINIMUM_PAGE);
    }

    public boolean hasPreviousPage(int currentPage) {
        return MINIMUM_PAGE < currentPage;
    }

    public boolean hasNextPage(int currentPage) {
        return this.totalPages() > currentPage;
    }

    public List<T> results(int currentPage) {
        new Validator<>(currentPage).rule(ValidationRules.isGreaterThan(MINIMUM_PAGE - 1)).validate();
        int indexMax = this.pageSize * currentPage;
        int indexMin = Math.min(indexMax - this.pageSize, this.originalList.size());
        int totalPages = this.totalPages();
        indexMax = Math.min(indexMax, this.originalList.size());

        if (indexMin >= indexMax) {
            throw new IllegalStateException("Page is too large. Maximum page number is " + totalPages);
        }
        return this.originalList.subList(indexMin, indexMax);
    }

    public static <T> void displayList(@NotNull Audience audience,
                                       int page,
                                       @NotNull String title,
                                       Function<T, Component> toMessage,
                                       Collection<T> options) {
        CommandPager<T> pager = new CommandPager<>(options);
        List<T> toDisplay = pager.results(page);
        audience.sendMessage(Component
                                     .text("====[")
                                     .color(NamedTextColor.AQUA)
                                     .append(Component.text(title + " (page " + page + "/" + pager.totalPages()))
                                     .append(Component.text(")]===").color(NamedTextColor.AQUA)));
        toDisplay.forEach(message -> audience.sendMessage(toMessage.apply(message)));
        Component pageNext = Component.empty();

        if (pager.hasPreviousPage(page)) {
            Component component = Component
                    .text("<<Previous<<")
                    .clickEvent(ClickEvent.runCommand("warps list " + (page - 1)));
            pageNext = pageNext.append(component);
        }
        if (pager.hasNextPage(page)) {
            if (pager.hasPreviousPage(page)) {
                Component splitComponent = Component.text(" | ");
                pageNext = pageNext.append(splitComponent);
            }
            Component nextPage = Component.text(">>Next>>").clickEvent(ClickEvent.runCommand("warps list " + page + 1));
            pageNext = pageNext.append(nextPage);
        }
        if (!pageNext.equals(Component.empty())) {
            audience.sendMessage(pageNext);
        }
    }
}
