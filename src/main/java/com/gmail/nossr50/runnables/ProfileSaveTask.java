package com.gmail.nossr50.runnables;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.util.Users;

public class ProfileSaveTask implements Runnable {
    private PlayerProfile playerProfile;

    public ProfileSaveTask(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    @Override
    public void run() {
        playerProfile.save();

        if (!playerProfile.getPlayer().isOnline()) {
            Users.getProfiles().remove(playerProfile);
        }
    }
}
