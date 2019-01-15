package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.scheduler.BukkitRunnable;

public class PowerLevelUpdatingTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!ScoreboardManager.powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
