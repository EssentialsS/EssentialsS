package org.essentialss.implementation.player.data.player;

import org.essentialss.api.message.MuteType;
import org.essentialss.implementation.player.data.AbstractProfileData;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurateException;

import java.util.UUID;

public class TestApplyChangesFromPlayerData {

    private final class TestProfileData extends AbstractProfileData {

        private final UUID uuid = UUID.randomUUID();

        @Override
        public @NotNull String playerName() {
            return "test";
        }

        @Override
        public @NotNull UUID uuid() {
            return this.uuid;
        }

        @Override
        public void reloadFromConfig() throws ConfigurateException {

        }

        @Override
        public void saveToConfig() throws ConfigurateException {

        }
    }

    @Test
    public void testAbstractApplyChanges() {
        //setup
        TestProfileData copyTo = new TestProfileData();
        TestProfileData copyFrom = new TestProfileData();

        copyFrom.setMuteTypes(MuteType.MESSAGE, MuteType.PRIVATE);
        copyFrom.setCanLooseItemsWhenUsed(true);
        copyFrom.setCommandSpying(true);

        //act
        copyTo.applyChangesFrom(copyFrom);

        //test
        Assertions.assertTrue(copyTo.muteTypes().contains(MuteType.PRIVATE), "MuteType of PRIVATE did not copy over");
        Assertions.assertTrue(copyTo.muteTypes().contains(MuteType.MESSAGE), "MuteType of MESSAGE did not copy over");
        Assertions.assertTrue(copyTo.canLooseItemsWhenUsed(), "Can loose items when used did not copy over");
        Assertions.assertTrue(copyTo.isCommandSpying(), "is command spying did not copy over");
    }


}
