package com.gmail.nossr50.runnables.player;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

public class PlayerProfileSaveTask extends BukkitRunnable {
    private PlayerProfile playerProfile;

    public PlayerProfileSaveTask(McMMOPlayer mcMMOPlayer) {
        this.playerProfile = mcMMOPlayer.getProfile();
    }

    @Override
    public void run() {
        playerProfile.save();
    }
}
