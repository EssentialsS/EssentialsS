package org.essentialss.messages;

import org.essentialss.api.config.configs.MessageConfig;
import org.essentialss.api.message.MessageAdapters;
import org.essentialss.api.message.MessageManager;
import org.essentialss.api.message.placeholder.SPlaceHolder;
import org.essentialss.api.message.placeholder.SPlaceHolders;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.api.utils.arrays.UnmodifiableCollectors;
import org.essentialss.config.configs.SMessageConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

public class SMessageManagerImpl implements MessageManager {

    private final MessageAdapters adapters;
    private final Collection<SPlaceHolder<?>> placeholders = new LinkedTransferQueue<>(SPlaceHolders.defaultValues());
    private final Map<Locale, SMessageConfigImpl> configs = new HashMap<>();
    private boolean hasUpdatedConfig;

    public SMessageManagerImpl() {
        SMessageConfigImpl englishConfig = new SMessageConfigImpl(Locale.ENGLISH);
        this.configs.put(Locale.ENGLISH, englishConfig);
        this.adapters = new SMessageAdaptersImpl(englishConfig);
    }

    @Override
    public @NotNull MessageAdapters adapters() {
        if (!this.hasUpdatedConfig) {
            this.configs.values().forEach(config -> {
                try {
                    config.update(this.adapters);
                } catch (ConfigurateException e) {
                    throw new RuntimeException(e);
                }
            });
            this.hasUpdatedConfig = true;
        }
        return this.adapters;
    }

    @Override
    public @NotNull Singleton<MessageConfig> config(@NotNull Locale locale) {
        return new Singleton<>(() -> {
            SMessageConfigImpl config = this.configs.get(locale);
            if (null == config) {
                config = new SMessageConfigImpl(locale);
                this.configs.put(locale, config);
            }
            return config;
        });
    }

    @Override
    public @NotNull <T> UnmodifiableCollection<SPlaceHolder<T>> mappedPlaceholdersFor(@NotNull Class<T> type) {
        return this.placeholders.stream().filter(ph -> {
            Class<?> phType = ph.type();
            return phType.isAssignableFrom(type);
        }).map(ph -> (SPlaceHolder<T>) ph).collect(UnmodifiableCollectors.asUnordered());
    }

    @Override
    public @NotNull UnmodifiableCollection<SPlaceHolder<?>> placeholdersFor(@NotNull String tagType) {
        return this.placeholders.stream().filter(ph -> ph.placeholderTagType().equals(tagType)).collect(UnmodifiableCollectors.asUnordered());
    }

    @Override
    public @NotNull UnmodifiableCollection<SPlaceHolder<?>> placeholdersFor(@NotNull Class<?> type) {
        return this.placeholders.stream().filter(ph -> ph.type().isAssignableFrom(type)).collect(UnmodifiableCollectors.asUnordered());
    }

    @Override
    public void register(@NotNull SPlaceHolder<?> placeholder) {
        this.placeholders.add(placeholder);
    }
}
