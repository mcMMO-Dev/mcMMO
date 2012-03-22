package com.gmail.nossr50;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.entity.Player;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Users {
    private static volatile Users instance;

    String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";
    String directory = "plugins/mcMMO/FlatFileStuff/";
    String directoryb = "plugins/mcMMO/FlatFileStuff/Leaderboards/";

    public static HashMap<String, PlayerProfile> players = new HashMap<String, PlayerProfile>();

    /**
     * Load users.
     */
    public void loadUsers() {
        new File(directory).mkdir();
        new File(directoryb).mkdir();
        File theDir = new File(location);

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
        if (!players.containsKey(player.getName().toLowerCase())) {
            players.put(player.getName().toLowerCase(), new PlayerProfile(player.getName()));
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
    public static HashMap<String, PlayerProfile> getProfiles() {
        return players;
    }

    /**
     * Remove a user from the database.
     *
     * @param player The player to remove
     */
    public static void removeUser(Player player) {

        //Only remove PlayerProfile if user is offline and we have it in memory
        if (!player.isOnline() && players.containsKey(player.getName().toLowerCase())) {
            players.get(player.getName().toLowerCase()).save();
            players.remove(player.getName().toLowerCase());
        }
    }

    /**
     * Remove a user from the DB by name.
     *
     * @param playerName The name of the player to remove
     */
    public static void removeUserByName(String playerName) {
        players.remove(playerName.toLowerCase());
    }

    /**
     * Get the profile of an online player.
     *
     * @param player The player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfile(Player player) {
        if(players.get(player.getName().toLowerCase()) != null) {
            return players.get(player.getName().toLowerCase());
        }
        else {
            players.put(player.getName().toLowerCase(), new PlayerProfile(player.getName()));
            return players.get(player.getName().toLowerCase());
        }
    }
    
    /**
     * Get the profile of an online player.
     *
     * @param player The player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getProfile(String playerName) {
        if(players.get(playerName.toLowerCase()) != null) {
            return players.get(playerName.toLowerCase());
        }
        else {
            players.put(playerName.toLowerCase(), new PlayerProfile(playerName));
            return players.get(playerName.toLowerCase());
        }
    }

    /**
     * Get the profile of an offline player.
     *
     * @param playerName Name of the player whose profile to retrieve
     * @return the player's profile
     */
    public static PlayerProfile getOfflineProfile(String playerName) {
        return new PlayerProfile(playerName, false);
    }

    /**
     * Get an instance of this class.
     *
     * @return an instance of this class
     */
    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }
}
