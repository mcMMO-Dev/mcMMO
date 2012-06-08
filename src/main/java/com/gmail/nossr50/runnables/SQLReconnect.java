package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.util.Database;
import com.gmail.nossr50.util.Users;

public class SQLReconnect implements Runnable {
    private final mcMMO plugin;

    public SQLReconnect(mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!Database.isConnected()) {
            Database.connect();
            if (Database.isConnected()) {
                for (PlayerProfile playerProfile : Users.getProfiles()) {
                    playerProfile.save(); //Save all profiles
                }

                Users.clearUsers(); //Clear the profiles
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    Users.addUser(player); //Add in new profiles, forcing them to 'load' again from MySQL
                }
            }
        }
    }
}
