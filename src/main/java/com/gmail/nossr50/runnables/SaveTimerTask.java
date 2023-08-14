package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.tcoded.folialib.wrapper.WrappedTask;

import java.util.concurrent.TimeUnit;

public class SaveTimerTask {
    public WrappedTask runTaskTimer(long delay, long period) {
        return mcMMO.p.getFoliaLib().getImpl().runTimer(() -> {
            LogUtils.debug(mcMMO.p.getLogger(), "[User Data] Saving...");
            // All player data will be saved periodically through this
            int count = 1;

            for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
                new PlayerProfileSaveTask(mcMMOPlayer.getProfile(), false).runTaskLaterAsynchronously(mcMMO.p, count);
                count++;
            }


            PartyManager.saveParties();
        }, delay * Misc.TICK_CONVERSION_FACTOR, period * Misc.TICK_CONVERSION_FACTOR, TimeUnit.MILLISECONDS);
    }
}
