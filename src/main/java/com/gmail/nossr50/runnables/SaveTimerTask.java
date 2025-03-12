package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.player.UserManager;

public class SaveTimerTask extends CancellableRunnable {
    @Override
    public void run() {
        LogUtils.debug(mcMMO.p.getLogger(), "[User Data] Saving...");
        // All player data will be saved periodically through this
        int count = 1;

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
            mcMMO.p.getFoliaLib().getScheduler().runLaterAsync(new PlayerProfileSaveTask(mcMMOPlayer.getProfile(), false), count);
            count++;
        }

        if (mcMMO.p.getPartyConfig().isPartyEnabled())
            mcMMO.p.getPartyManager().saveParties();
    }
}
