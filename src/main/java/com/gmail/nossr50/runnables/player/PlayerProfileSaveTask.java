package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfileSaveTask extends BukkitRunnable {
    private PlayerProfile playerProfile;

    public PlayerProfileSaveTask(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    @Override
    public void run() {
        boolean saveSuccess = playerProfile.save();

        if(!saveSuccess)
        {
            playerProfile.scheduleAsyncSaveDelay();
        }
    }
}
