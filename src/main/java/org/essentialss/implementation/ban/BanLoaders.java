package org.essentialss.implementation.ban;

import org.essentialss.implementation.ban.account.AccountBanLoaderImpl;
import org.essentialss.implementation.ban.ip.IPAddressBanLoader;

public interface BanLoaders {

    AccountBanLoaderImpl ACCOUNT_BAN = new AccountBanLoaderImpl();
    IPAddressBanLoader IP_BAN = new IPAddressBanLoader();

}
