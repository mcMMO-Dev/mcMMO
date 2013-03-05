package com.gmail.nossr50.runnables;

import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.player.UserManager;

public class SaveTimerTask implements Runnable {
    @Override
    public void run() {
        // All player data will be saved periodically through this
        int count = 1;
        BukkitScheduler bukkitScheduler = mcMMO.p.getServer().getScheduler();

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers().values()) {
            bukkitScheduler.scheduleSyncDelayedTask(mcMMO.p, new PlayerProfileSaveTask(mcMMOPlayer), count);
            count++;
        }

        PartyManager.saveParties();
    }
}
