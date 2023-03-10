package org.essentialss.implementation.messages;

import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.message.MessageAdapters;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.SingleUnmodifiableCollection;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.implementation.config.configs.SMessageConfigImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public class SMessageManagerImpl implements MessageManager {

    private final MessageAdapters adapters;
    private final Collection<SPlaceHolder<?>> placeholders = new LinkedTransferQueue<>(SPlaceHolders.defaultValues());
    private final MessageConfig config = new SMessageConfigImpl();

    public SMessageManagerImpl() {
        this.adapters = new SMessageAdaptersImpl(this.config);
    }

    @Override
    public @NotNull MessageAdapters adapters() {
        return this.adapters;
    }

    @Override
    public @NotNull Singleton<MessageConfig> config() {
        return new Singleton<>(() -> this.config);
    }

    @Override
    public @NotNull <T> UnmodifiableCollection<SPlaceHolder<T>> mappedPlaceholdersFor(@NotNull Class<T> type) {
        return new SingleUnmodifiableCollection<>(
                this.placeholders.stream().filter(ph -> ph.type().isAssignableFrom(type)).map(ph -> (SPlaceHolder<T>) ph).collect(Collectors.toList()));
    }

    @Override
    public @NotNull UnmodifiableCollection<SPlaceHolder<?>> placeholdersFor(@NotNull Class<?> type) {
        return new SingleUnmodifiableCollection<>(this.placeholders.stream().filter(ph -> ph.type().isAssignableFrom(type)).collect(Collectors.toList()));
    }

    @Override
    public void register(@NotNull SPlaceHolder<?> placeholder) {
        this.placeholders.add(placeholder);
    }
}
