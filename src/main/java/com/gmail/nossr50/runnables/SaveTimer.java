package com.gmail.nossr50.runnables;

import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public class SaveTimer implements Runnable {
    @Override
    public void run() {
        //All player data will be saved periodically through this
        int count = 1;
        BukkitScheduler bukkitScheduler = mcMMO.p.getServer().getScheduler();

        for (McMMOPlayer mcMMOPlayer : Users.getPlayers().values()) {
            bukkitScheduler.scheduleSyncDelayedTask(mcMMO.p, new ProfileSaveTask(mcMMOPlayer), count);
            count++;
        }

        PartyManager.saveParties();
    }
}
