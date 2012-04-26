package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Database;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class SQLReconnect implements Runnable {
    private final mcMMO plugin;

    public SQLReconnect(mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (Database.isConnected()) {
            Database.connect();
            if (Database.isConnected()) {
                for (PlayerProfile x : Users.players.values()) {
                    x.save(); //Save all profiles
                }

                Users.players.clear(); //Clear the profiles
                for (Player x : plugin.getServer().getOnlinePlayers()) {
                    Users.addUser(x); //Add in new profiles, forcing them to 'load' again from MySQL
                }
            }
        }
    }
}
