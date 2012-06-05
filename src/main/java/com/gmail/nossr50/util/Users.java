package com.gmail.nossr50.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Users {
    private static HashMap<Player, PlayerProfile> players = new HashMap<Player, PlayerProfile>();

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
        if (!players.containsKey(player)) {
            players.put(player, new PlayerProfile(player, true));
        }
    }

    /**
     * Clear all users.
     */
    public static void clearUsers() {
        players.clear();
    }

    /**
     * Get all PlayerProfiles.
     *
     * @return a HashMap containing the PlayerProfile of everyone in the database
     */
    public static HashMap<Player, PlayerProfile> getProfiles() {
        return players;
    }

    /**
     * Remove a user from the database.
     *
     * @param player The player to remove
     */
    public static void removeUser(Player player) {
        //Only remove PlayerProfile if user is offline and we have it in memory
        if (!player.isOnline() && players.containsKey(player)) {
            players.get(player).save();
            players.remove(player);
        }
    }

    /**
     * Remove a user from the DB by name.
     *
     * @param playerName The name of the player to remove
     */
    public static void removeUserByName(String playerName) {
        players.remove(mcMMO.p.getServer().getOfflinePlayer(playerName));
    }

    /**
     * Get the profile of a player.
     *
     * @param player The player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfile(OfflinePlayer player) {
        return players.get(player);
    }

    /**
     * Get the profile of a player by name.
     *
     * @param player The name of the player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfileByName(String playerName) {
        Player player = mcMMO.p.getServer().getPlayer(playerName);
        PlayerProfile profile = players.get(player);

        if (profile == null) {
            if (player != null) {
                PlayerProfile newProfile = new PlayerProfile(player, true);

                players.put(player, newProfile);
                return newProfile;
            }
            else {
                mcMMO.p.getLogger().severe("getProfileByName(" + playerName + ") just returned null :(");

                for (StackTraceElement ste : new Throwable().getStackTrace()) {
                    System.out.println(ste);
                }

                return null;
            }
        }
        else {
            return profile;
        }
    }
}
