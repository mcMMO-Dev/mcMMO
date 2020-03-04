package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;

public class PlayerProfileSaveTask implements Consumer<Task> {
    private PlayerProfile playerProfile;
    private boolean isSync;

    public PlayerProfileSaveTask(PlayerProfile playerProfile, boolean isSync) {
        this.playerProfile = playerProfile;
        this.isSync = isSync;
    }

    @Override
    public void accept(Task task) {
        playerProfile.save(isSync);
    }
}
