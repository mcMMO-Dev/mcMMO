package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Users;

public class RemoveProfileFromMemoryTask implements Runnable {
    private Player player;

    public RemoveProfileFromMemoryTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        //Check if the profile still exists (stuff like MySQL reconnection removes profiles)
        if (Users.getProfiles().containsKey(player)) {
            Users.getProfile(player).save(); //We save here so players don't quit/reconnect to cause lag
            Users.removeUser(player);
        }
    }
}
