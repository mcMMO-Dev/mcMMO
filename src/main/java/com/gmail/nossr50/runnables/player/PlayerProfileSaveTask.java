package com.gmail.nossr50.runnables.player;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.util.player.UserManager;

public class PlayerProfileSaveTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private PlayerProfile playerProfile;

    public PlayerProfileSaveTask(McMMOPlayer mcMMOPlayer) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.playerProfile = mcMMOPlayer.getProfile();
    }

    @Override
    public void run() {
        playerProfile.save();

        Player player = mcMMOPlayer.getPlayer();

        if (!player.isOnline()) {
            UserManager.remove(player.getName());
        }
    }
}
