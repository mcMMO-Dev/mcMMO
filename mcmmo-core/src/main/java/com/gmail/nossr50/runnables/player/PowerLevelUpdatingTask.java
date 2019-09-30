package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

public class PowerLevelUpdatingTask extends BukkitRunnable {

    private final mcMMO pluginRef;

    public PowerLevelUpdatingTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public void run() {
        if (!pluginRef.getScoreboardManager().powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
