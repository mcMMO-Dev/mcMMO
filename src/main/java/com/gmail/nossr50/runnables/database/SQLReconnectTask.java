package com.gmail.nossr50.runnables.database;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.util.player.UserManager;

public class SQLReconnectTask extends BukkitRunnable {
    @Override
    public void run() {
        if (DatabaseManager.checkConnected()) {
            UserManager.saveAll();  // Save all profiles
            UserManager.clearAll(); // Clear the profiles

            for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                UserManager.addUser(player); // Add in new profiles, forcing them to 'load' again from MySQL
            }
        }
    }
}
