package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTimerTask extends BukkitRunnable {
    @Override
    public void run() {
        mcMMO.p.debug("[User Data] Saving...");
        // All player data will be saved periodically through this
        int count = 1;

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
            new PlayerProfileSaveTask(mcMMOPlayer.getProfile(), false).runTaskLaterAsynchronously(mcMMO.p, count);
            count++;
        }


        PartyManager.saveParties();
    }
}
