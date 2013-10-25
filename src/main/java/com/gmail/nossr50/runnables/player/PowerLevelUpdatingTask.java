package com.gmail.nossr50.runnables.player;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class PowerLevelUpdatingTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!ScoreboardManager.powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
