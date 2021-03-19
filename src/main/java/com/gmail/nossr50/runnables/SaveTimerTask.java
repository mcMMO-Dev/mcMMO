package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTimerTask extends BukkitRunnable {
    @Override
    public void run() {
        mcMMO.p.debug("[User Data] Saving...");
        // All player data will be saved periodically through this
        int count = 1;

        //TODO: write a more efficient bulk save
        for (McMMOPlayer mmoPlayer : UserManager.getPlayers()) {
            UserManager.getPlayerSaveHandler().scheduleAsyncSaveDelay(mmoPlayer.getPlayerData());
            count++;
        }

        mcMMO.getPartyManager().saveParties();
    }
}
