package com.gmail.nossr50.core.runnables.player;


import com.gmail.nossr50.core.util.scoreboards.ScoreboardManager;

public class PowerLevelUpdatingTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!ScoreboardManager.powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
