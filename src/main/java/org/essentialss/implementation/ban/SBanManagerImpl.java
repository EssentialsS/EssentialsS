package org.essentialss.implementation.ban;

import org.essentialss.api.ban.SBanManager;
import org.essentialss.api.ban.data.AccountBanData;
import org.essentialss.api.ban.data.BanData;
import org.essentialss.api.ban.data.MacAddressBanData;
import org.essentialss.api.config.BanConfig;
import org.essentialss.api.utils.Singleton;
import org.essentialss.api.utils.arrays.UnmodifiableCollection;
import org.essentialss.implementation.EssentialsSMain;
import org.essentialss.implementation.ban.account.AccountBanImpl;
import org.essentialss.implementation.ban.ip.IPBanDataImpl;
import org.essentialss.implementation.config.SBanConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.LinkedTransferQueue;

public class SBanManagerImpl implements SBanManager {

    private final @NotNull LinkedTransferQueue<BanData<?>> data = new LinkedTransferQueue<>();

    private final Singleton<BanConfig> config = new Singleton<>(() -> {
        BanConfig config = new SBanConfigImpl();
        if (!config.file().exists()) {
            try {
                config.generateDefault();
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }
        return config;
    });

    @Override
    public UnmodifiableCollection<BanData<?>> banData() {
        return new UnmodifiableCollection<>(this.data);
    }

    @Override
    public Singleton<BanConfig> banConfig() {
        return this.config;
    }

    @Override
    public AccountBanImpl banAccount(@NotNull GameProfile account, @Nullable LocalDateTime date) {
        AccountBanImpl banData = new AccountBanImpl(account, date);
        this.data.add(banData);
        return banData;
    }

    @Override
    public IPBanDataImpl banIp(@NotNull String hostname,
                               @Nullable String lastKnown,
                               @Nullable LocalDateTime localDateTime) {
        IPBanDataImpl banData = new IPBanDataImpl(hostname, lastKnown, localDateTime);
        this.data.add(banData);
        return banData;
    }

    @Override
    public MacAddressBanData banMacAddress(byte[] address,
                                           @Nullable String lastKnown,
                                           @Nullable LocalDateTime localDateTime) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void unban(@NotNull BanData<?> data) {
        this.data.remove(data);
    }

    @Override
    public void saveToConfig() throws ConfigurateException {
        Path path = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory();
        File file = new File(path.toFile(), "data/banned.conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.load();

        ConfigurationNode accountBans = root.node("account");
        for (AccountBanData banData : this.banData(AccountBanData.class)) {
            ConfigurationNode node = accountBans.appendListNode();
            banData.loader().saveTo(node, banData);
        }
        loader.save(root);
    }

    @Override
    public void reloadFromConfig() throws ConfigurateException {
        Path path = Sponge.configManager().pluginConfig(EssentialsSMain.plugin().container()).directory();
        File file = new File(path.toFile(), "data/banned.conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(file).build();
        CommentedConfigurationNode root = loader.load();

        Collection<BanData<?>> banData = new LinkedHashSet<>();

        ConfigurationNode accountBans = root.node("account");
        for (ConfigurationNode banDataNode : accountBans.childrenList()) {
            AccountBanData accountBan = BanLoaders.ACCOUNT_BAN.loadFrom(banDataNode);
            if (null == accountBan) {
                continue;
            }
            banData.add(accountBan);
        }

        this.data.clear();
        this.data.addAll(banData);
    }
}
