package com.gmail.nossr50.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Users {
    private static List<PlayerProfile> profiles = new ArrayList<PlayerProfile>();

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
     */
    public static void addUser(Player player) {
        String playerName = player.getName();

        for (Iterator<PlayerProfile> it = profiles.iterator() ; it.hasNext() ; ) {
            PlayerProfile playerProfile = it.next();

            if (playerProfile.getPlayerName().equals(playerName)) {
                //The player object is different on each reconnection and must be updated
                playerProfile.setPlayer(player);
                return;
            }
        }

        //New player, or already removed from the list
        profiles.add(new PlayerProfile(player, true));
    }

    /**
     * Clear all users.
     */
    public static void clearUsers() {
        profiles.clear();
    }

    /**
     * Get all PlayerProfiles.
     *
     * @return a HashMap containing the PlayerProfile of everyone in the database
     */
    public static List<PlayerProfile> getProfiles() {
        return profiles;
    }

    /**
     * Remove a user from the database.
     *
     * @param player The player to remove
     */
    public static void removeUser(OfflinePlayer player) {
        removeUser(player.getName());
    }

    /**
     * Remove a user from the DB by name.
     *
     * @param playerName The name of the player to remove
     */
    public static void removeUser(String playerName) {
        for (Iterator<PlayerProfile> it = profiles.iterator() ; it.hasNext() ; ) {
            if (it.next().getPlayerName().equals(playerName)) {
                it.remove();
                return;
            }
        }
    }

    /**
     * Remove a user from the DB by its profile.
     * 
     * @param playerProfile the profile of the player to remove
     */
    public static void removeUser(PlayerProfile playerProfile) {
        profiles.remove(playerProfile);
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
        for (Iterator<PlayerProfile> it = profiles.iterator() ; it.hasNext() ; ) {
            PlayerProfile playerProfile = it.next();

            if (playerProfile.getPlayerName().equals(playerName)) {
                return playerProfile;
            }
        }

        return null;
    }
}
