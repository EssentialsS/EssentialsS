package org.essentialss.ban;

import org.essentialss.ban.account.AccountBanLoaderImpl;
import org.essentialss.ban.ip.IPAddressBanLoader;

public interface BanLoaders {

    AccountBanLoaderImpl ACCOUNT_BAN = new AccountBanLoaderImpl();
    IPAddressBanLoader IP_BAN = new IPAddressBanLoader();

}
