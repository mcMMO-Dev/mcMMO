package com.gmail.nossr50.runnables.player;

import org.bukkit.scheduler.BukkitRunnable;

public class PowerLevelUpdatingTask extends BukkitRunnable {
    @Override
    public void run() {
        if (!pluginRef.getScoreboardManager().powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
