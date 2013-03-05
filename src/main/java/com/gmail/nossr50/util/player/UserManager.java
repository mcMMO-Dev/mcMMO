package com.gmail.nossr50.util.player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

public final class UserManager {
    private static Map<String, McMMOPlayer> players = new HashMap<String, McMMOPlayer>();

    private UserManager() {};

    /**
     * Load users.
     */
    public static void loadUsers() {
        new File(mcMMO.getFlatFileDirectory()).mkdir();

        try {
            new File(mcMMO.getUsersFilePath()).createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new user.
     *
     * @param player The player to create a user record for
     * @return the player's {@link McMMOPlayer} object
     */
    public static McMMOPlayer addUser(Player player) {
        String playerName = player.getName();
        McMMOPlayer mcMMOPlayer = players.get(playerName);

        if (mcMMOPlayer != null) {
            mcMMOPlayer.setPlayer(player); // The player object is different on each reconnection and must be updated
        }
        else {
            mcMMOPlayer = new McMMOPlayer(player);
            players.put(playerName, mcMMOPlayer);
        }

        return mcMMOPlayer;
    }

    /**
     * Remove a user.
     *
     * @param playerName The name of the player to remove
     */
    public static void remove(String playerName) {
        players.remove(playerName);
    }

    /**
     * Clear all users.
     */
    public static void clearAll() {
        players.clear();
    }

    /**
     * Save all users.
     */
    public static void saveAll() {
        for (McMMOPlayer mcMMOPlayer : players.values()) {
            mcMMOPlayer.getProfile().save();
        }
    }

    public static Map<String, McMMOPlayer> getPlayers() {
        return players;
    }

    /**
     * Get the profile of a player.
     *
     * @param player The player whose profile to retrieve
     * @return the player's profile
     */
    @Deprecated
    public static PlayerProfile getProfile(OfflinePlayer player) {
        return getProfile(player.getName());
    }

    /**
     * Get the profile of a player by name.
     *
     * @param playerName The name of the player whose profile to retrieve
     * @return the player's profile
     */
    @Deprecated
    public static PlayerProfile getProfile(String playerName) {
        McMMOPlayer mcmmoPlayer = players.get(playerName);

        return (mcmmoPlayer != null) ? mcmmoPlayer.getProfile() : null;
    }

    /**
     * Get the McMMOPlayer of a player by name.
     *
     * @param playerName The name of the player whose McMMOPlayer to retrieve
     * @return the player's McMMOPlayer object
     */
    public static McMMOPlayer getPlayer(String playerName) {
        return players.get(playerName);
    }

    /**
     * Get the McMMOPlayer of a player.
     *
     * @param player The player whose McMMOPlayer to retrieve
     * @return the player's McMMOPlayer object
     */
    public static McMMOPlayer getPlayer(OfflinePlayer player) {
        return players.get(player.getName());
    }
}
