package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;

import java.util.UUID;

public class DatabaseAPI {

    /**
     * Checks if a player exists in the mcMMO Database
     * @param uuid player UUID
     * @return true if the player exists in the DB, false if they do not
     */
    public boolean doesPlayerExistInDB(String uuid) {
        return doesPlayerExistInDB(UUID.fromString(uuid));
    }

    /**
     * Checks if a player exists in the mcMMO Database
     * @param uuid player UUID
     * @return true if the player exists in the DB, false if they do not
     */
    public boolean doesPlayerExistInDB(UUID uuid) {
        PlayerProfile playerProfile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);

        return playerProfile.isLoaded();
    }

}
