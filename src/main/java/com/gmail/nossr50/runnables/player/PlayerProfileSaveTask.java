package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfileSaveTask extends BukkitRunnable {
    private PlayerProfile playerProfile;
    private boolean isSync;

    public PlayerProfileSaveTask(PlayerProfile playerProfile, boolean isSync) {
        this.playerProfile = playerProfile;
        this.isSync = isSync;
    }

    @Override
    public void run() {
        playerProfile.save(isSync);
    }
}
