package com.gmail.nossr50.database;

import java.util.List;
import java.util.Map;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

public interface DatabaseManager {
    public final long PURGE_TIME = 2630000000L * Config.getInstance().getOldUsersCutoff();

    /**
     * Purge users with 0 power level from the database.
     */
    public void purgePowerlessUsers();

    /**
     * Purge users who haven't logged on in over a certain time frame from the database.
     */
    public void purgeOldUsers();

    /**
     * Remove a user from the database.
     *
     * @param playerName The name of the user to remove
     * @return true if the user was successfully removed, false otherwise
     */
    public boolean removeUser(String playerName);

    /**
     * Save a user to the database.
     *
     * @param profile The profile of the player to save
     */
    public void saveUser(PlayerProfile profile);

    /**
    * Retrieve leaderboard info.
    *
    * @param skillName The skill to retrieve info on
    * @param pageNumber Which page in the leaderboards to retrieve
    * @param statsPerPage The number of stats per page
    * @return the requested leaderboard information
    */
    public List<PlayerStat> readLeaderboard(String skillName, int pageNumber, int statsPerPage);

    /**
     * Retrieve rank info.
     *
     * @param playerName The name of the user to retrieve the rankings for
     * @return the requested rank information
     */
    public Map<String, Integer> readRank(String playerName);

    /**
     * Add a new user to the database.
     *
     * @param playerName The name of the player to be added to the database
     */
    public void newUser(String playerName);

    /**
     * Load a player from the database.
     *
     * @param playerName The name of the player to load from the database
     * @return The player's data
     */
    public List<String> loadPlayerData(String playerName);

    /**
     * Convert player data to a different storage format.
     *
     * @param data The player's data
     * @return true if the conversion was successful, false otherwise
     * @throws Exception
     */
    public boolean convert(String[] data) throws Exception;
}
