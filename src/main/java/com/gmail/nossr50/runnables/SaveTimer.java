package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;

public class SaveTimer implements Runnable {
    private final mcMMO plugin;

    public SaveTimer(final mcMMO plugin) {
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
