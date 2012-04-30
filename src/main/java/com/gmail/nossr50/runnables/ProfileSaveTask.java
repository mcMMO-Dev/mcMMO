package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Users;

public class ProfileSaveTask implements Runnable {
    Player player = null;

    public ProfileSaveTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player != null) {
            Users.getProfileByName(player.getName()).save();
        }
    }
}
