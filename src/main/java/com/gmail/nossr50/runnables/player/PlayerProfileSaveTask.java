package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.util.player.UserManager;

public class PlayerProfileSaveTask implements Runnable {
    private McMMOPlayer mcMMOPlayer;
    private PlayerProfile playerProfile;

    public PlayerProfileSaveTask(McMMOPlayer mcMMOPlayer) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.playerProfile = mcMMOPlayer.getProfile();
    }

    @Override
    public void run() {
        playerProfile.save();

        if (!mcMMOPlayer.getPlayer().isOnline()) {
            UserManager.remove(playerProfile.getPlayerName());
        }
    }
}
