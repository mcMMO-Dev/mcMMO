package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.util.Users;

public class ProfileSaveTask implements Runnable {
    private McMMOPlayer mcMMOPlayer;
    private PlayerProfile playerProfile;

    public ProfileSaveTask(McMMOPlayer mcMMOPlayer) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.playerProfile = mcMMOPlayer.getProfile();
    }

    @Override
    public void run() {
        playerProfile.save();

        if (!mcMMOPlayer.getPlayer().isOnline()) {
            Users.remove(playerProfile.getPlayerName());
        }
    }
}
