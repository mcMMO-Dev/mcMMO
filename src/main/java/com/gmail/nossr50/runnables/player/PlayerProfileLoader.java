package com.gmail.nossr50.runnables.player;

import java.util.concurrent.Callable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

public class PlayerProfileLoader implements Callable<PlayerProfile> {
    private final String playerName;

    public PlayerProfileLoader(String player) {
        this.playerName = player;
    }

    @Override
    public PlayerProfile call() {
        return mcMMO.getDatabaseManager().loadPlayerProfile(playerName, true);
    }
}
