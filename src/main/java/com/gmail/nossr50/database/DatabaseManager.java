package com.gmail.nossr50.database;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.api.exceptions.ProfileRetrievalException;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.player.MMODataSnapshot;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DatabaseManager {
    // One month in milliseconds
    long PURGE_TIME = 2630000000L * Config.getInstance().getOldUsersCutoff();
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
     * @param uuid uuid of player to remove, can be null
     * @return true if the user was successfully removed, false otherwise
     */
    boolean removeUser(@NotNull String playerName, @Nullable UUID uuid);

    /**
     * Removes any cache used for faster lookups
     * Currently only used for SQL
     * @param uuid target UUID to cleanup
     */
    void removeCache(@NotNull UUID uuid);

    /**
     * Save a user to the database.
     *
     * @param mmoDataSnapshot Snapshot of player data to save
     */
    boolean saveUser(@NotNull MMODataSnapshot mmoDataSnapshot);

    /**
    * Retrieve leaderboard info.
     * Will never be null but it may be empty
    *
    * @param skill The skill to retrieve info on
    * @param pageNumber Which page in the leaderboards to retrieve
    * @param statsPerPage The number of stats per page
    * @return the requested leaderboard information
    */
    @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill, int pageNumber, int statsPerPage) throws InvalidSkillException;

    /**
     * Retrieve rank info into a HashMap from PrimarySkillType to the rank.
     * <p>
     * The special value <code>null</code> is used to represent the Power
     * Level rank (the combination of all skill levels).
     *
     * @param playerName The name of the user to retrieve the rankings for
     * @return the requested rank information
     */
    @NotNull Map<PrimarySkillType, Integer> readRank(@NotNull String playerName);

    /**
     * Add a new user to the database.
     *  @param playerName The name of the player to be added to the database
     * @param uuid The uuid of the player to be added to the database
     */
    void insertNewUser(@NotNull String playerName, @NotNull UUID uuid) throws Exception;

    @Nullable PlayerProfile queryPlayerDataByPlayer(@NotNull Player player) throws ProfileRetrievalException, NullArgumentException;

    /**
     * Load player data (in the form of {@link PlayerProfile}) if player data exists
     * Returns null if it doesn't
     *
     * @param uuid The uuid of the player to load from the database
     * @param playerName the current player name for this player
     * @return The player's data, or null if not found
     */
    @Nullable PlayerProfile queryPlayerDataByUUID(@NotNull UUID uuid, @NotNull String playerName) throws ProfileRetrievalException, NullArgumentException;

    /**
     * This method queries the DB for player data for target player
     * If it fails to find data for this player, or if it does find data but the data is corrupted,
     *  it will then proceed to make brand new data for the target player, which will be saved to the DB during the next save
     *
     * This method will return null for all other errors, which indicates a problem with the DB, in which case mcMMO
     *  will try to load the player data periodically, but that isn't handled in this method
     *
     * @param player target player
     * @return {@link PlayerProfile} for the target player
     */
    @Nullable PlayerProfile initPlayerProfile(@NotNull Player player) throws Exception;

    /**
     * Get all users currently stored in the database.
     *
     * @return list of playernames
     */
    @NotNull List<String> getStoredUsers();

    /**
     * Convert all users from this database to the provided database using
     * {@link #saveUser(MMODataSnapshot)}.
     *
     * @param destination The DatabaseManager to save to
     */
    void convertUsers(@NotNull DatabaseManager destination);

//    boolean saveUserUUID(String userName, UUID uuid);

//    boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs);

    /**
     * Retrieve the type of database in use. Custom databases should return CUSTOM.
     *
     * @return The type of database
     */
    @NotNull DatabaseType getDatabaseType();

    /**
     * Called when the plugin disables
     */
    void onDisable();
}
