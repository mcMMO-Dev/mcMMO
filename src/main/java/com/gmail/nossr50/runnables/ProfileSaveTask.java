package com.gmail.nossr50.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Users;

public class ProfileSaveTask implements Runnable {
    private Player player;

    public ProfileSaveTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player != null) {
            Users.getProfile(player).save();
        }
    }
}
