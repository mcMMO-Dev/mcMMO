package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTimerTask extends BukkitRunnable {
    @Override
    public void run() {
        // All player data will be saved periodically through this
        int count = 1;

        for (McMMOPlayer mcMMOPlayer : pluginRef.getUserManager().getPlayers()) {
            new PlayerProfileSaveTask(mcMMOPlayer.getProfile(), false).runTaskLaterAsynchronously(pluginRef, count);
            count++;
        }

        pluginRef.getPartyManager().saveParties();
    }
}
