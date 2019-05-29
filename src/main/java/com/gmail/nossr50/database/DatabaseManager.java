package com.gmail.nossr50.database;

import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DatabaseManager {
    // One month in milliseconds
    long PURGE_TIME = 2630000000L * mcMMO.getDatabaseCleaningSettings().getOldUserCutoffMonths();
    // During convertUsers, how often to output a status
    int progressInterval = 200;

    /**
     * Purge users with 0 power level from the database.
     */
    void purgePowerlessUsers();

    /**
     * Purge users who haven't logged on in over a certain time frame from the database.
     */
    void purgeOldUsers();

    /**
     * Remove a user from the database.
     *
     * @param playerName The name of the user to remove
     * @param uuid player UUID, can be null
     * @return true if the user was successfully removed, false otherwise
     */
    public boolean removeUser(String playerName, UUID uuid);

    /**
     * Removes any cache used for faster lookups
     * Currently only used for SQL
     * @param uuid target UUID to cleanup
     */
    public void cleanupUser(UUID uuid);

    /**
     * Save a user to the database.
     *
     * @param profile The profile of the player to save
     * @return true if successful, false on failure
     */
    boolean saveUser(PlayerProfile profile);

    /**
     * Retrieve leaderboard info.
     *
     * @param skill        The skill to retrieve info on
     * @param pageNumber   Which page in the leaderboards to retrieve
     * @param statsPerPage The number of stats per page
     * @return the requested leaderboard information
     */
    List<PlayerStat> readLeaderboard(PrimarySkillType skill, int pageNumber, int statsPerPage);

    /**
     * Retrieve rank info into a HashMap from PrimarySkillType to the rank.
     * <p>
     * The special value <code>null</code> is used to represent the Power
     * Level rank (the combination of all skill levels).
     *
     * @param playerName The name of the user to retrieve the rankings for
     * @return the requested rank information
     */
    Map<PrimarySkillType, Integer> readRank(String playerName);

    /**
     * Add a new user to the database.
     *
     * @param playerName The name of the player to be added to the database
     * @param uuid       The uuid of the player to be added to the database
     */
    void newUser(String playerName, UUID uuid);

    /**
     * Load a player from the database.
     *
     * @param playerName The name of the player to load from the database
     * @param createNew  Whether to create a new record if the player is not
     *                   found
     * @return The player's data, or an unloaded PlayerProfile if not found
     * and createNew is false
     * @deprecated replaced by {@link #loadPlayerProfile(String playerName, UUID uuid, boolean createNew)}
     */
    @Deprecated
    PlayerProfile loadPlayerProfile(String playerName, boolean createNew);

    /**
     * Load a player from the database.
     *
     * @param uuid The uuid of the player to load from the database
     * @return The player's data, or an unloaded PlayerProfile if not found
     */
    PlayerProfile loadPlayerProfile(UUID uuid);

    /**
     * Load a player from the database. Attempt to use uuid, fall back on playername
     *
     * @param playerName The name of the player to load from the database
     * @param uuid       The uuid of the player to load from the database
     * @param createNew  Whether to create a new record if the player is not
     *                   found
     * @return The player's data, or an unloaded PlayerProfile if not found
     * and createNew is false
     */
    PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean createNew);

    /**
     * Get all users currently stored in the database.
     *
     * @return list of playernames
     */
    List<String> getStoredUsers();

    /**
     * Convert all users from this database to the provided database using
     * {@link #saveUser(PlayerProfile)}.
     *
     * @param destination The DatabaseManager to save to
     */
    void convertUsers(DatabaseManager destination);

    boolean saveUserUUID(String userName, UUID uuid);

    boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs);

    /**
     * Retrieve the type of database in use. Custom databases should return CUSTOM.
     *
     * @return The type of database
     */
    DatabaseType getDatabaseType();

    /**
     * Called when the plugin disables
     */
    void onDisable();
}
