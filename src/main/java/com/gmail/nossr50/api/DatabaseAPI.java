package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class DatabaseAPI {
    private DatabaseAPI() {
    }

    /**
     * Checks if a player exists in the mcMMO Database
     *
     * @param offlinePlayer target player
     * @return true if the player exists in the DB, false if they do not
     */
    public static boolean doesPlayerExistInDB(@NotNull OfflinePlayer offlinePlayer) {
        PlayerProfile playerProfile = mcMMO.getDatabaseManager().loadPlayerProfile(offlinePlayer);

        return playerProfile.isLoaded();
    }

    /**
     * Checks if a player exists in the mcMMO Database
     *
     * @param uuid target player
     * @return true if the player exists in the DB, false if they do not
     */
    public static boolean doesPlayerExistInDB(@NotNull UUID uuid) {
        PlayerProfile playerProfile = null;
        try {
            playerProfile = mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
        } catch (Exception e) {
            return false;
        }

        return playerProfile.isLoaded();
    }

    /**
     * Checks if a player exists in the mcMMO Database
     *
     * @param playerName target player
     * @return true if the player exists in the DB, false if they do not
     */
    public static boolean doesPlayerExistInDB(@NotNull String playerName) {
        PlayerProfile playerProfile = mcMMO.getDatabaseManager().loadPlayerProfile(playerName);

        return playerProfile.isLoaded();
    }

}
