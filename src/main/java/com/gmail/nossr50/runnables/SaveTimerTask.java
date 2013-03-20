package com.gmail.nossr50.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.util.player.UserManager;

public class SaveTimerTask extends BukkitRunnable {
    @Override
    public void run() {
        // All player data will be saved periodically through this
        int count = 1;

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers().values()) {
            new PlayerProfileSaveTask(mcMMOPlayer).runTaskLater(mcMMO.p, count);
            count++;
        }

        PartyManager.saveParties();
    }
}
