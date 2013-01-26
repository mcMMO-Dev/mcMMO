package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.util.Users;

public class SQLReconnect implements Runnable {
    @Override
    public void run() {
        if (Database.checkConnected()) {
            Users.saveAll(); //Save all profiles
            Users.clearAll(); //Clear the profiles

            for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
                Users.addUser(player); //Add in new profiles, forcing them to 'load' again from MySQL
            }
        }
    }
}
