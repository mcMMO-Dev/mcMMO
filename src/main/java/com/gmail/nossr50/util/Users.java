package com.gmail.nossr50.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Users {
    private static Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();

    /**
     * Load users.
     */
    public static void loadUsers() {
        new File(mcMMO.flatFileDirectory).mkdir();
        new File(mcMMO.leaderboardDirectory).mkdir();

        File theDir = new File(mcMMO.usersFile);

        if (!theDir.exists()) {
            try {
                FileWriter writer = new FileWriter(theDir);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a new user.
     *
     * @param player The player to create a user record for
     * @return the player's profile
     */
    public static PlayerProfile addUser(Player player) {
        String playerName = player.getName();
        PlayerProfile playerProfile = profiles.get(playerName);

        if (playerProfile != null) {
            //The player object is different on each reconnection and must be updated
            playerProfile.setPlayer(player);
        }
        else {
            playerProfile = new PlayerProfile(player, playerName, true);

            profiles.put(playerName, playerProfile);
        }

        return playerProfile;
    }

    /*
     * Remove a user.
     * 
     * @param playerName The name of the player to remove
     */
    public static void remove(String playerName) {
        profiles.remove(playerName);
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        profiles.clear();
    }

    /*
     * Save all users.
     */
    public static void saveAll() {
        for (PlayerProfile playerProfile : profiles.values()) {
            playerProfile.save();
        }
    }

    /**
     * Get all PlayerProfiles.
     *
     * @return a HashMap containing the PlayerProfile of everyone in the database
     */
    public static Map<String, PlayerProfile> getProfiles() {
        return profiles;
    }

    /**
     * Get the profile of a player.
     *
     * @param player The player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfile(OfflinePlayer player) {
        return getProfile(player.getName());
    }

    /**
     * Get the profile of a player by name.
     *
     * @param player The name of the player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfile(String playerName) {
        return profiles.get(playerName);
    }
}
