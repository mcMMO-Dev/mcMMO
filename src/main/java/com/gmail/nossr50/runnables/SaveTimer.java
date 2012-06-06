package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.McMMO;

public class SaveTimer implements Runnable {
    private final McMMO plugin;

    public SaveTimer(final McMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //All player data will be saved periodically through this
        int count = 1;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ProfileSaveTask(player), count);
            count++;
        }
    }
}
