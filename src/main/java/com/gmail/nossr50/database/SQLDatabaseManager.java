package com.gmail.nossr50.database;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.player.UniqueDataType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.UUIDUpdateAsyncTask;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillTools;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SQLDatabaseManager implements DatabaseManager {

    // ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    public static final String MOBHEALTHBAR_VARCHAR = "VARCHAR(50)";
    public static final String UUID_VARCHAR = "VARCHAR(36)";
    public static final String USER_VARCHAR = "VARCHAR(40)";
    public static final int CHILD_SKILLS_SIZE = 2;
    public static final String LEGACY_DRIVER_PATH = "com.mysql.jdbc.Driver";
    private static final String ALL_QUERY_VERSION = "total";
    private static final String INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_";

    /**
     * utf8mb4 is the "real" UTF-8, unlike MySQL's legacy "utf8".
     */
    private static final String CHARSET_SQL = "utf8mb4";

    private static final PrimarySkillType[] PERSISTED_SKILLS = {
            PrimarySkillType.TAMING,
            PrimarySkillType.MINING,
            PrimarySkillType.REPAIR,
            PrimarySkillType.WOODCUTTING,
            PrimarySkillType.UNARMED,
            PrimarySkillType.HERBALISM,
            PrimarySkillType.EXCAVATION,
            PrimarySkillType.ARCHERY,
            PrimarySkillType.SWORDS,
            PrimarySkillType.AXES,
            PrimarySkillType.ACROBATICS,
            PrimarySkillType.FISHING,
            PrimarySkillType.ALCHEMY,
            PrimarySkillType.CROSSBOWS,
            PrimarySkillType.TRIDENTS,
            PrimarySkillType.MACES,
            PrimarySkillType.SPEARS
    };

    // ---------------------------------------------------------------------
    // Instance fields
    // ---------------------------------------------------------------------

    private final String tablePrefix = mcMMO.p.getGeneralConfig().getMySQLTablePrefix();
    private final Logger logger;

    /**
     * Cache of user IDs by UUID. Concurrent for cross-thread DB usage.
     */
    private final Map<UUID, Integer> cachedUserIDs = new ConcurrentHashMap<>();
    private final ReentrantLock massUpdateLock = new ReentrantLock();
    private DataSource miscPool;
    private DataSource loadPool;
    private DataSource savePool;

    // ---------------------------------------------------------------------
    // Construction / pool setup
    // ---------------------------------------------------------------------

    SQLDatabaseManager(Logger logger, String driverPath) {
        this.logger = Objects.requireNonNull(logger, "logger");

        final String connectionString = buildConnectionStringWithOptions();

        if (!loadDriver(driverPath)) {
            logger.severe(
                    "Neither MySQL driver was found; aborting SQLDatabaseManager initialization.");
            return;
        }

        // Set up pools
        final var config = mcMMO.p.getGeneralConfig();
        this.miscPool = createDataSource(
                driverPath,
                connectionString,
                config.getMySQLMaxPoolSize(PoolIdentifier.MISC),
                config.getMySQLMaxConnections(PoolIdentifier.MISC)
        );
        this.savePool = createDataSource(
                driverPath,
                connectionString,
                config.getMySQLMaxPoolSize(PoolIdentifier.SAVE),
                config.getMySQLMaxConnections(PoolIdentifier.SAVE)
        );
        this.loadPool = createDataSource(
                driverPath,
                connectionString,
                config.getMySQLMaxPoolSize(PoolIdentifier.LOAD),
                config.getMySQLMaxConnections(PoolIdentifier.LOAD)
        );

        checkStructure();
    }

    @NotNull
    private static String getConnectionString() {
        final var general = mcMMO.p.getGeneralConfig();
        String connectionString = "jdbc:mysql://" + general.getMySQLServerName()
                + ":" + general.getMySQLServerPort() + "/"
                + general.getMySQLDatabaseName();

        // Temporary hack for 1.17 + SSL support (legacy path kept intact)
        if (!mcMMO.getCompatibilityManager().getMinecraftGameVersion().isAtLeast(1, 17, 0)
                && general.getMySQLSSL()) {
            connectionString += "?verifyServerCertificate=false&useSSL=true&requireSSL=true";
        } else {
            connectionString += "?useSSL=false";
        }
        return connectionString;
    }

    @NotNull
    private String buildConnectionStringWithOptions() {
        String connectionString = getConnectionString();

        if (mcMMO.p.getGeneralConfig().getMySQLPublicKeyRetrieval()) {
            connectionString += "&allowPublicKeyRetrieval=true";
        }
        return connectionString;
    }

    private boolean loadDriver(String driverPath) {
        try {
            Class.forName(driverPath);
            return true;
        } catch (ClassNotFoundException primary) {
            try {
                Class.forName(LEGACY_DRIVER_PATH);
                logger.info("Primary driver not found; using legacy MySQL driver: "
                        + LEGACY_DRIVER_PATH);
                return true;
            } catch (ClassNotFoundException legacy) {
                logger.log(Level.SEVERE, "Initial driver path load failed", primary);
                logger.log(Level.SEVERE, "Legacy driver path load failed", legacy);
                return false;
            }
        }
    }

    private DataSource createDataSource(String driverPath,
            String connectionString,
            int maxIdle,
            int maxActive) {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setDriverClassName(driverPath);
        poolProps.setUrl(connectionString);
        poolProps.setUsername(mcMMO.p.getGeneralConfig().getMySQLUserName());
        poolProps.setPassword(mcMMO.p.getGeneralConfig().getMySQLUserPassword());

        poolProps.setInitialSize(0);
        poolProps.setMaxIdle(maxIdle);
        poolProps.setMaxActive(maxActive);
        poolProps.setMaxWait(-1);
        poolProps.setRemoveAbandoned(true);
        poolProps.setRemoveAbandonedTimeout(60);
        poolProps.setTestOnBorrow(true);
        poolProps.setValidationQuery("SELECT 1");
        poolProps.setValidationInterval(30_000);

        return new DataSource(poolProps);
    }

    // ---------------------------------------------------------------------
    // Public operations
    // ---------------------------------------------------------------------

    public int purgePowerlessUsers() {
        massUpdateLock.lock();
        logger.info("Purging powerless users...");

        int purged = 0;

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            purged = statement.executeUpdate(
                    "DELETE FROM " + tablePrefix + "skills WHERE "
                            + "taming = 0 AND mining = 0 AND woodcutting = 0 AND repair = 0 "
                            + "AND unarmed = 0 AND herbalism = 0 AND excavation = 0 AND "
                            + "archery = 0 AND swords = 0 AND axes = 0 AND acrobatics = 0 "
                            + "AND fishing = 0 AND alchemy = 0 AND crossbows = 0 AND tridents = 0 "
                            + "AND maces = 0 AND spears = 0;"
            );

            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "experience` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "experience`.`user_id` = `s`.`user_id`)"
            );
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "huds` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "huds`.`user_id` = `s`.`user_id`)"
            );
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "cooldowns` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "cooldowns`.`user_id` = `s`.`user_id`)"
            );
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "users` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "users`.`id` = `s`.`user_id`)"
            );
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            massUpdateLock.unlock();
        }

        logger.info("Purged " + purged + " users from the database.");
        return purged;
    }

    public void purgeOldUsers() {
        massUpdateLock.lock();
        long months = mcMMO.p.getPurgeTime() / 2_630_000_000L;
        logger.info("Purging inactive users older than " + months + " months...");

        int purged = 0;

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                Statement statement = connection.createStatement()) {

            purged = statement.executeUpdate(
                    "DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                            "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                            "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                            "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                            "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                            "WHERE ((UNIX_TIMESTAMP() - lastlogin) > " + mcMMO.p.getPurgeTime()
                            + ")"
            );
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            massUpdateLock.unlock();
        }

        logger.info("Purged " + purged + " users from the database.");
    }

    public boolean removeUser(String playerName, UUID uuid) {
        boolean success = false;

        String sql = "DELETE FROM u, e, h, s, c " +
                "USING " + tablePrefix + "users u " +
                "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                "WHERE u.`user` = ?";

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, playerName);
            success = statement.executeUpdate() != 0;
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        if (success) {
            if (uuid != null) {
                cleanupUser(uuid);
            }
            Misc.profileCleanup(playerName);
        }

        return success;
    }

    public void cleanupUser(UUID uuid) {
        cachedUserIDs.remove(uuid);
    }

    @Override
    public boolean saveUser(PlayerProfile profile) {
        final String playerName = profile.getPlayerName();
        final UUID uuid = profile.getUniqueId();

        try (Connection connection = getConnection(PoolIdentifier.SAVE)) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                int userId = getUserID(connection, playerName, uuid);
                if (userId == -1) {
                    userId = newUser(connection, playerName, uuid);
                    if (userId == -1) {
                        logger.severe("Failed to create new account for " + playerName);
                        connection.rollback();
                        return false;
                    }
                }

                if (!updateLastLogin(connection, userId, playerName)
                        || !updateSkills(connection, userId, profile, playerName)
                        || !updateExperience(connection, userId, profile, playerName)
                        || !updateCooldowns(connection, userId, profile, playerName)
                        || !updateHudSettings(connection, userId, profile, playerName)) {
                    connection.rollback();
                    return false;
                }

                connection.commit();
                connection.setAutoCommit(originalAutoCommit);
                return true;
            } catch (SQLException e) {
                connection.rollback();
                logSQLException(e);
                return false;
            } finally {
                // Best-effort restore
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // ignore
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // Update helpers
    // ---------------------------------------------------------------------

    private boolean updateLastLogin(Connection connection, int userId, String playerName) {
        String sql =
                "UPDATE " + tablePrefix + "users SET lastlogin = UNIX_TIMESTAMP() WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            if (stmt.executeUpdate() == 0) {
                logger.severe("Failed to update last login for " + playerName);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    private boolean updateSkills(Connection connection, int userId, PlayerProfile profile,
            String playerName) {
        String sql = "UPDATE " + tablePrefix + "skills SET "
                + " taming = ?, mining = ?, repair = ?, woodcutting = ?"
                + ", unarmed = ?, herbalism = ?, excavation = ?"
                + ", archery = ?, swords = ?, axes = ?, acrobatics = ?"
                + ", fishing = ?, alchemy = ?, crossbows = ?, tridents = ?, maces = ?, spears = ?, total = ?"
                + " WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.TAMING));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.MINING));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.REPAIR));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.WOODCUTTING));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.UNARMED));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.HERBALISM));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.EXCAVATION));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.ARCHERY));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.SWORDS));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.AXES));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.ACROBATICS));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.FISHING));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.ALCHEMY));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.CROSSBOWS));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.TRIDENTS));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.MACES));
            stmt.setInt(i++, profile.getSkillLevel(PrimarySkillType.SPEARS));

            int total = 0;
            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                total += profile.getSkillLevel(primarySkillType);
            }
            stmt.setInt(i++, total);
            stmt.setInt(i, userId);

            if (stmt.executeUpdate() == 0) {
                logger.severe("Failed to update skills for " + playerName);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    private boolean updateExperience(Connection connection, int userId, PlayerProfile profile,
            String playerName) {
        String sql = "UPDATE " + tablePrefix + "experience SET "
                + " taming = ?, mining = ?, repair = ?, woodcutting = ?"
                + ", unarmed = ?, herbalism = ?, excavation = ?"
                + ", archery = ?, swords = ?, axes = ?, acrobatics = ?"
                + ", fishing = ?, alchemy = ?, crossbows = ?, tridents = ?, maces = ?, spears = ?"
                + " WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.TAMING));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.MINING));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.REPAIR));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.WOODCUTTING));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.UNARMED));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.HERBALISM));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.EXCAVATION));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.ARCHERY));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.SWORDS));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.AXES));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.ACROBATICS));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.FISHING));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.ALCHEMY));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.CROSSBOWS));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.TRIDENTS));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.MACES));
            stmt.setInt(i++, profile.getSkillXpLevel(PrimarySkillType.SPEARS));
            stmt.setInt(i, userId);

            if (stmt.executeUpdate() == 0) {
                logger.severe("Failed to update experience for " + playerName);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    private boolean updateCooldowns(Connection connection, int userId, PlayerProfile profile,
            String playerName) {
        String sql = "UPDATE " + tablePrefix + "cooldowns SET "
                + "  mining = ?, woodcutting = ?, unarmed = ?"
                + ", herbalism = ?, excavation = ?, swords = ?"
                + ", axes = ?, blast_mining = ?, chimaera_wing = ?, crossbows = ?"
                + ", tridents = ?, maces = ?, spears = ?"
                + " WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.SUPER_BREAKER));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.TREE_FELLER));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.BERSERK));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.GREEN_TERRA));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.SERRATED_STRIKES));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.SKULL_SPLITTER));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.BLAST_MINING));
            stmt.setLong(i++, profile.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.SUPER_SHOTGUN));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.TRIDENTS_SUPER_ABILITY));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.MACES_SUPER_ABILITY));
            stmt.setLong(i++, profile.getAbilityDATS(SuperAbilityType.SPEARS_SUPER_ABILITY));
            stmt.setInt(i, userId);

            if (stmt.executeUpdate() == 0) {
                logger.severe("Failed to update cooldowns for " + playerName);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    private boolean updateHudSettings(Connection connection, int userId, PlayerProfile profile,
            String playerName) {
        String sql = "UPDATE " + tablePrefix
                + "huds SET mobhealthbar = ?, scoreboardtips = ? WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, MobHealthbarType.HEARTS.name());
            stmt.setInt(2, profile.getScoreboardTipsShown());
            stmt.setInt(3, userId);

            if (stmt.executeUpdate() == 0) {
                logger.severe("Failed to update hud settings for " + playerName);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // Leaderboards / rank
    // ---------------------------------------------------------------------

    public @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill,
            int pageNumber,
            int statsPerPage) throws InvalidSkillException {
        List<PlayerStat> stats = new ArrayList<>();

        // Fix for a plugin that people are using that is throwing SQL errors
        if (skill != null && SkillTools.isChildSkill(skill)) {
            logger.severe(
                    "A plugin hooking into mcMMO is being naughty with our database commands, update all plugins that hook into mcMMO and contact their devs!"
            );
            throw new InvalidSkillException(
                    "A plugin hooking into mcMMO that you are using is attempting to read leaderboard skills for child skills, child skills do not have leaderboards! This is NOT an mcMMO error!"
            );
        }

        String query = (skill == null)
                ? ALL_QUERY_VERSION
                : skill.name().toLowerCase(Locale.ENGLISH);

        String sql = "SELECT " + query + ", `user` FROM " + tablePrefix + "users " +
                "JOIN " + tablePrefix + "skills ON (user_id = id) " +
                "WHERE " + query + " > 0 " +
                "AND NOT `user` = '\\_INVALID\\_OLD\\_USERNAME\\_' " +
                "ORDER BY " + query + " DESC, `user` LIMIT ?, ?";

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, (pageNumber * statsPerPage) - statsPerPage);
            statement.setInt(2, statsPerPage);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // 1st col = value, 2nd col = username
                    int value = resultSet.getInt(1);
                    String playerName = resultSet.getString(2);
                    stats.add(new PlayerStat(playerName, value));
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        return stats;
    }

    public Map<PrimarySkillType, Integer> readRank(String playerName) {
        // NOTE: We keep HashMap so we can still use `null` as the "total" key,
        // just like the original code.
        Map<PrimarySkillType, Integer> ranks = new HashMap<>();

        // Preload this player's skill levels & total in a single query
        try (Connection connection = getConnection(PoolIdentifier.MISC)) {

            // 1) Load all relevant skill levels for this player in one shot
            Map<PrimarySkillType, Integer> levels = new EnumMap<>(PrimarySkillType.class);
            int totalLevel = 0;

            String loadSql =
                    "SELECT s.*, u.`user` " +
                            "FROM " + tablePrefix + "users u " +
                            "JOIN " + tablePrefix + "skills s ON s.user_id = u.id " +
                            "WHERE u.`user` = ?";

            try (PreparedStatement stmt = connection.prepareStatement(loadSql)) {
                stmt.setString(1, playerName);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        // Player not found in DB, no ranks to report
                        return ranks;
                    }

                    for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                        String column = primarySkillType.name().toLowerCase(Locale.ENGLISH);
                        levels.put(primarySkillType, rs.getInt(column));
                    }

                    totalLevel = rs.getInt(ALL_QUERY_VERSION); // "total" column
                }
            }

            // Helper method to compute a rank (base + tie offset + 1)
            // for any numeric column on the skills table.
            class RankCalculator {
                int computeRank(String columnName, int value) throws SQLException {
                    if (value <= 0) {
                        // Original logic effectively did not assign a rank when the value <= 0
                        return -1;
                    }

                    // Base: number of players with strictly higher value
                    String higherSql =
                            "SELECT COUNT(*) AS cnt " +
                                    "FROM " + tablePrefix + "users u " +
                                    "JOIN " + tablePrefix + "skills s ON s.user_id = u.id " +
                                    "WHERE s." + columnName + " > ?";

                    int higherCount = 0;
                    try (PreparedStatement stmt = connection.prepareStatement(higherSql)) {
                        stmt.setInt(1, value);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                higherCount = rs.getInt("cnt");
                            }
                        }
                    }

                    // Tie offset: number of players with the same value whose username
                    // sorts alphabetically before this player's name.
                    String tieSql =
                            "SELECT COUNT(*) AS cnt " +
                                    "FROM " + tablePrefix + "users u " +
                                    "JOIN " + tablePrefix + "skills s ON s.user_id = u.id " +
                                    "WHERE s." + columnName + " = ? " +
                                    "AND s." + columnName + " > 0 " +
                                    "AND u.`user` < ?";

                    int tieCount = 0;
                    try (PreparedStatement stmt = connection.prepareStatement(tieSql)) {
                        stmt.setInt(1, value);
                        stmt.setString(2, playerName);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                tieCount = rs.getInt("cnt");
                            }
                        }
                    }

                    // 1-based rank: higher values first, then alphabetical by username
                    return higherCount + tieCount + 1;
                }
            }

            RankCalculator rankCalculator = new RankCalculator();

            // 2) Per-skill rank
            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                int level = levels.getOrDefault(primarySkillType, 0);

                int rank = rankCalculator.computeRank(
                        primarySkillType.name().toLowerCase(Locale.ENGLISH),
                        level
                );

                if (rank > 0) {
                    ranks.put(primarySkillType, rank);
                }
            }

            // 3) Total rank (null key matches original behavior)
            if (totalLevel > 0) {
                int totalRank = rankCalculator.computeRank(ALL_QUERY_VERSION, totalLevel);
                if (totalRank > 0) {
                    ranks.put(null, totalRank);
                }
            }

        } catch (SQLException ex) {
            logSQLException(ex);
        }

        return ranks;
    }


    // ---------------------------------------------------------------------
    // New user / load profile
    // ---------------------------------------------------------------------

    public @NotNull PlayerProfile newUser(String playerName, UUID uuid) {
        try (Connection connection = getConnection(PoolIdentifier.MISC)) {
            newUser(connection, playerName, uuid);
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        return new PlayerProfile(
                playerName,
                uuid,
                true,
                mcMMO.p.getAdvancedConfig().getStartingLevel()
        );
    }

    @Override
    public @NotNull PlayerProfile newUser(@NotNull Player player) {
        try (Connection connection = getConnection(PoolIdentifier.SAVE)) {
            int id = newUser(connection, player.getName(), player.getUniqueId());

            if (id == -1) {
                return new PlayerProfile(
                        player.getName(),
                        player.getUniqueId(),
                        false,
                        mcMMO.p.getAdvancedConfig().getStartingLevel()
                );
            } else {
                return loadPlayerProfile(player);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                    "Unexpected SQLException while creating new user for " + player.getName(), e);
        }

        return new PlayerProfile(
                player.getName(),
                player.getUniqueId(),
                false,
                mcMMO.p.getAdvancedConfig().getStartingLevel()
        );
    }

    private int newUser(Connection connection, String playerName, @Nullable UUID uuid) {
        Objects.requireNonNull(connection, "connection must not be null");

        if (playerName == null || playerName.isEmpty()) {
            logger.severe("Attempted to create user with null/empty playerName");
            return -1;
        }

        // Step 1: Invalidate any existing rows with the same username
        String invalidateSql =
                "UPDATE `" + tablePrefix + "users` " +
                        "SET `user` = ? " +
                        "WHERE `user` = ?";

        try (PreparedStatement invalidateStmt = connection.prepareStatement(invalidateSql)) {
            invalidateStmt.setString(1, INVALID_OLD_USERNAME);
            invalidateStmt.setString(2, playerName);
            invalidateStmt.executeUpdate();
        } catch (SQLException ex) {
            logSQLException(ex);
            return -1;
        }

        // Step 2: Insert the new user and fetch the generated id
        String insertSql =
                "INSERT INTO " + tablePrefix +
                        "users (user, uuid, lastlogin) VALUES (?, ?, UNIX_TIMESTAMP())";

        try (PreparedStatement insertStmt = connection.prepareStatement(
                insertSql, Statement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, playerName);
            insertStmt.setString(2, uuid != null ? uuid.toString() : null);
            insertStmt.executeUpdate();

            try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    logger.severe(
                            "Unable to create new user account in DB for player '" + playerName
                                    + "'");
                    return -1;
                }

                int userId = keys.getInt(1);
                writeMissingRows(connection, userId);
                return userId;
            }
        } catch (SQLException ex) {
            logSQLException(ex);
            return -1;
        }
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull String playerName) {
        try {
            return loadPlayerFromDB(null, playerName);
        } catch (RuntimeException e) {
            mcMMO.p.getLogger().log(Level.SEVERE,
                    "Unexpected error while loading player profile for " + playerName, e);
            return new PlayerProfile(
                    playerName,
                    false,
                    mcMMO.p.getAdvancedConfig().getStartingLevel()
            );
        }
    }

    @Override
    public @NotNull PlayerProfile loadPlayerProfile(@NotNull OfflinePlayer offlinePlayer) {
        return loadPlayerFromDB(offlinePlayer.getUniqueId(), offlinePlayer.getName());
    }

    public @NotNull PlayerProfile loadPlayerProfile(@NotNull UUID uuid,
            @Nullable String playerName) {
        return loadPlayerFromDB(uuid, playerName);
    }

    @Override
    public @NotNull PlayerProfile loadPlayerProfile(@NotNull UUID uuid) {
        return loadPlayerFromDB(uuid, null);
    }

    private PlayerProfile loadPlayerFromDB(@Nullable UUID uuid, @Nullable String playerName)
            throws IllegalArgumentException {

        if (uuid == null && playerName == null) {
            throw new IllegalArgumentException(
                    "Error looking up player, both UUID and playerName are null and one must not be."
            );
        }

        try (Connection connection = getConnection(PoolIdentifier.LOAD)) {
            int id = getUserID(connection, playerName, uuid);

            if (id == -1) {
                return createEmptyProfile(playerName, uuid);
            }

            writeMissingRows(connection, id);

            String sql =
                    "SELECT " +
                            // --- skills (levels) ---
                            "s.taming      AS skill_taming, " +
                            "s.mining      AS skill_mining, " +
                            "s.repair      AS skill_repair, " +
                            "s.woodcutting AS skill_woodcutting, " +
                            "s.unarmed     AS skill_unarmed, " +
                            "s.herbalism   AS skill_herbalism, " +
                            "s.excavation  AS skill_excavation, " +
                            "s.archery     AS skill_archery, " +
                            "s.swords      AS skill_swords, " +
                            "s.axes        AS skill_axes, " +
                            "s.acrobatics  AS skill_acrobatics, " +
                            "s.fishing     AS skill_fishing, " +
                            "s.alchemy     AS skill_alchemy, " +
                            "s.crossbows   AS skill_crossbows, " +
                            "s.tridents    AS skill_tridents, " +
                            "s.maces       AS skill_maces, " +
                            "s.spears      AS skill_spears, " +

                            // --- skills XP ---
                            "e.taming      AS xp_taming, " +
                            "e.mining      AS xp_mining, " +
                            "e.repair      AS xp_repair, " +
                            "e.woodcutting AS xp_woodcutting, " +
                            "e.unarmed     AS xp_unarmed, " +
                            "e.herbalism   AS xp_herbalism, " +
                            "e.excavation  AS xp_excavation, " +
                            "e.archery     AS xp_archery, " +
                            "e.swords      AS xp_swords, " +
                            "e.axes        AS xp_axes, " +
                            "e.acrobatics  AS xp_acrobatics, " +
                            "e.fishing     AS xp_fishing, " +
                            "e.alchemy     AS xp_alchemy, " +
                            "e.crossbows   AS xp_crossbows, " +
                            "e.tridents    AS xp_tridents, " +
                            "e.maces       AS xp_maces, " +
                            "e.spears      AS xp_spears, " +

                            // --- cooldowns / unique data ---
                            "c.mining        AS cd_super_breaker, " +
                            "c.repair        AS cd_repair_unused, " +
                            "c.woodcutting   AS cd_tree_feller, " +
                            "c.unarmed       AS cd_berserk, " +
                            "c.herbalism     AS cd_green_terra, " +
                            "c.excavation    AS cd_giga_drill_breaker, " +
                            "c.archery       AS cd_explosive_shot, " +
                            "c.swords        AS cd_serrated_strikes, " +
                            "c.axes          AS cd_skull_splitter, " +
                            "c.acrobatics    AS cd_acrobatics_unused, " +
                            "c.blast_mining  AS cd_blast_mining, " +
                            "c.chimaera_wing AS ud_chimaera_wing_dats, " +
                            "c.crossbows     AS cd_super_shotgun, " +
                            "c.tridents      AS cd_tridents_super_ability, " +
                            "c.maces         AS cd_maces_super_ability, " +
                            "c.spears        AS cd_spears_super_ability, " +

                            // --- HUD + user info ---
                            "h.mobhealthbar  AS mobhealthbar, " +
                            "h.scoreboardtips AS scoreboardtips, " +
                            "u.uuid          AS uuid, " +
                            "u.`user`        AS username " +
                            "FROM " + tablePrefix + "users u " +
                            "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                            "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                            "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                            "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                            "WHERE u.id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return createEmptyProfile(playerName, uuid);
                    }

                    String nameInDb = resultSet.getString("username");

                    if (shouldUpdateUsername(playerName, uuid, nameInDb)) {
                        invalidateOldUsername(connection, nameInDb);
                        updateCurrentUsername(connection, id, playerName, uuid);
                    }

                    if (playerName == null || playerName.isEmpty()) {
                        playerName = nameInDb;
                    }

                    return loadFromResult(playerName, resultSet);
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
            return createEmptyProfile(playerName, uuid);
        }
    }

    private PlayerProfile createEmptyProfile(@Nullable String playerName, @Nullable UUID uuid) {
        return new PlayerProfile(playerName, uuid, mcMMO.p.getAdvancedConfig().getStartingLevel());
    }

    private boolean shouldUpdateUsername(@Nullable String playerName,
            @Nullable UUID uuid,
            String nameInDb) {
        return playerName != null
                && !playerName.isEmpty()
                && !playerName.equalsIgnoreCase(nameInDb)
                && uuid != null;
    }

    private void invalidateOldUsername(Connection connection, String oldName) throws SQLException {
        String sql = "UPDATE `" + tablePrefix + "users` SET `user` = ? WHERE `user` = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, INVALID_OLD_USERNAME);
            stmt.setString(2, oldName);
            stmt.executeUpdate();
        }
    }

    private void updateCurrentUsername(Connection connection,
            int id,
            String playerName,
            UUID uuid) throws SQLException {
        String sql = "UPDATE `" + tablePrefix + "users` SET `user` = ?, uuid = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, uuid.toString());
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    private PlayerProfile loadFromResult(String playerName, ResultSet result) throws SQLException {
        final var skills = new EnumMap<PrimarySkillType, Integer>(PrimarySkillType.class);
        final var skillsXp = new EnumMap<PrimarySkillType, Float>(PrimarySkillType.class);
        final var skillsDATS = new EnumMap<SuperAbilityType, Integer>(SuperAbilityType.class);
        final var uniqueData = new EnumMap<UniqueDataType, Integer>(UniqueDataType.class);

        // --- Skills & XP by predictable alias name ---

        for (PrimarySkillType skill : PERSISTED_SKILLS) {
            String base = skill.name().toLowerCase(Locale.ROOT);

            int level = result.getInt("skill_" + base);
            float xp = result.getFloat("xp_" + base);

            skills.put(skill, level);
            skillsXp.put(skill, xp);
        }

        // --- Cooldowns / DATS ---

        skillsDATS.put(SuperAbilityType.SUPER_BREAKER,
                result.getInt("cd_super_breaker"));
        skillsDATS.put(SuperAbilityType.TREE_FELLER,
                result.getInt("cd_tree_feller"));
        skillsDATS.put(SuperAbilityType.BERSERK,
                result.getInt("cd_berserk"));
        skillsDATS.put(SuperAbilityType.GREEN_TERRA,
                result.getInt("cd_green_terra"));
        skillsDATS.put(SuperAbilityType.GIGA_DRILL_BREAKER,
                result.getInt("cd_giga_drill_breaker"));
        skillsDATS.put(SuperAbilityType.EXPLOSIVE_SHOT,
                result.getInt("cd_explosive_shot"));
        skillsDATS.put(SuperAbilityType.SERRATED_STRIKES,
                result.getInt("cd_serrated_strikes"));
        skillsDATS.put(SuperAbilityType.SKULL_SPLITTER,
                result.getInt("cd_skull_splitter"));
        skillsDATS.put(SuperAbilityType.BLAST_MINING,
                result.getInt("cd_blast_mining"));
        skillsDATS.put(SuperAbilityType.SUPER_SHOTGUN,
                result.getInt("cd_super_shotgun"));
        skillsDATS.put(SuperAbilityType.TRIDENTS_SUPER_ABILITY,
                result.getInt("cd_tridents_super_ability"));
        skillsDATS.put(SuperAbilityType.MACES_SUPER_ABILITY,
                result.getInt("cd_maces_super_ability"));
        skillsDATS.put(SuperAbilityType.SPEARS_SUPER_ABILITY,
                result.getInt("cd_spears_super_ability"));

        uniqueData.put(UniqueDataType.CHIMAERA_WING_DATS,
                result.getInt("ud_chimaera_wing_dats"));

        // --- HUD + UUID ---

        int scoreboardTipsShown;
        try {
            scoreboardTipsShown = result.getInt("scoreboardtips");
        } catch (SQLException | RuntimeException ignored) {
            scoreboardTipsShown = 0;
        }

        UUID uuid = null;
        try {
            String uuidString = result.getString("uuid");
            if (uuidString != null && !uuidString.isEmpty()) {
                uuid = UUID.fromString(uuidString);
            }
        } catch (SQLException | IllegalArgumentException ignored) {
            // keep uuid null
        }

        return new PlayerProfile(playerName, uuid, skills, skillsXp, skillsDATS,
                scoreboardTipsShown, uniqueData, null);
    }

    // ---------------------------------------------------------------------
    // Cross-database conversion
    // ---------------------------------------------------------------------

    public void convertUsers(DatabaseManager destination) {
        final List<String> usernames = getStoredUsers();
        if (usernames.isEmpty()) {
            logger.info("No stored users found to convert.");
            return;
        }

        int convertedUsers = 0;
        long startMillis = System.currentTimeMillis();
        int progressInterval = 1000; // use existing Misc.printProgress behavior

        for (String playerName : usernames) {
            try {
                final PlayerProfile profile = loadPlayerProfile(playerName);
                destination.saveUser(profile);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to convert user '" + playerName + "'", ex);
            }

            convertedUsers++;
            Misc.printProgress(convertedUsers, progressInterval, startMillis);
        }

        logger.info("Finished converting " + convertedUsers + " users.");
    }

    public boolean saveUserUUID(String userName, UUID uuid) {
        String sql = "UPDATE `" + tablePrefix + "users` SET uuid = ? WHERE `user` = ?";

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, userName);
            statement.execute();
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        String sql = "UPDATE " + tablePrefix + "users SET uuid = ? WHERE `user` = ?";
        int count = 0;

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Map.Entry<String, UUID> entry : fetchedUUIDs.entrySet()) {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey());
                statement.addBatch();

                count++;
                if (count % 500 == 0) {
                    statement.executeBatch();
                    count = 0;
                }
            }

            if (count != 0) {
                statement.executeBatch();
            }

            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    public List<String> getStoredUsers() {
        List<String> users = new ArrayList<>();

        String sql = "SELECT `user` FROM " + tablePrefix + "users";

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                users.add(resultSet.getString("user"));
            }
        } catch (SQLException e) {
            logSQLException(e);
        }

        return users;
    }

    // ---------------------------------------------------------------------
    // Schema / structure
    // ---------------------------------------------------------------------

    /**
     * Checks that the database structure is present and correct. Runs once on startup.
     */
    private void checkStructure() {
        try (Connection connection = getConnection(PoolIdentifier.MISC)) {
            final String schemaQuery =
                    "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = ?"
                            + " AND table_name = ?";

            try (PreparedStatement schemaStmt = connection.prepareStatement(schemaQuery)) {
                ensureUsersTable(connection, schemaStmt);
                ensureHudsTable(connection, schemaStmt);
                ensureCooldownsTable(connection, schemaStmt);
                ensureSkillsTable(connection, schemaStmt);
                ensureExperienceTable(connection, schemaStmt);
            }

            // Run upgrade steps
            for (UpgradeType updateType : UpgradeType.values()) {
                checkDatabaseStructure(connection, updateType);
            }

            // Optionally truncate skills to level caps
            if (mcMMO.p.getGeneralConfig().getTruncateSkills()) {
                truncateSkillsToCaps(connection);
            }

            // Clean up orphan rows
            deleteOrphans(connection);
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        // Column-level structure updates (adds newer skill columns if missing)
        final String skills = "skills";
        final String experience = "experience";
        final String cooldowns = "cooldowns";
        final String crossbows = "crossbows";
        final String tridents = "tridents";
        final String maces = "maces";
        final String spears = "spears";

        updateStructure(skills, crossbows, "32");
        updateStructure(skills, tridents, "32");
        updateStructure(skills, maces, "32");
        updateStructure(skills, spears, "32");

        updateStructure(experience, crossbows, "10");
        updateStructure(experience, tridents, "10");
        updateStructure(experience, maces, "10");
        updateStructure(experience, spears, "10");

        updateStructure(cooldowns, crossbows, "10");
        updateStructure(cooldowns, tridents, "10");
        updateStructure(cooldowns, maces, "10");
        updateStructure(cooldowns, spears, "10");
    }

    private void ensureUsersTable(Connection connection,
            PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "users")) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` ("
                + "`id` int AUTO_INCREMENT,"
                + "`user` varchar(40) NOT NULL,"
                + "`uuid` varchar(36),"
                + "`lastlogin` bigint NOT NULL,"
                + "PRIMARY KEY (`id`),"
                + "INDEX `user_index`(`user`),"
                + "UNIQUE(`uuid`))";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate(sql);
        }
    }

    private void ensureHudsTable(Connection connection,
            PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "huds")) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "huds` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`mobhealthbar` varchar(50) NOT NULL DEFAULT '"
                + mcMMO.p.getGeneralConfig().getMobHealthbarDefault() + "',"
                + "`scoreboardtips` int(10) NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=" + CHARSET_SQL + ";";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate(sql);
        }
    }

    private void ensureCooldownsTable(Connection connection,
            PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "cooldowns")) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "cooldowns` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`taming` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`mining` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`woodcutting` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`repair` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`unarmed` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`herbalism` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`excavation` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`archery` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`swords` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`axes` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`acrobatics` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`blast_mining` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`chimaera_wing` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`crossbows` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`tridents` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`maces` int(32) unsigned NOT NULL DEFAULT '0',"
                + "`spears` int(32) unsigned NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=" + CHARSET_SQL + ";";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate(sql);
        }
    }

    private void ensureSkillsTable(Connection connection,
            PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "skills")) {
            return;
        }

        int starting = mcMMO.p.getAdvancedConfig().getStartingLevel();
        String startingLevel = "'" + starting + "'";
        String totalLevel =
                "'" + (starting * (PrimarySkillType.values().length - CHILD_SKILLS_SIZE)) + "'";

        String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "skills` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`taming` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`mining` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`woodcutting` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`repair` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`unarmed` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`herbalism` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`excavation` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`archery` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`swords` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`axes` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`acrobatics` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`fishing` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`alchemy` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`crossbows` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`tridents` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`maces` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`spears` int(10) unsigned NOT NULL DEFAULT " + startingLevel + ","
                + "`total` int(10) unsigned NOT NULL DEFAULT " + totalLevel + ","
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=" + CHARSET_SQL + ";";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate(sql);
        }
    }

    private void ensureExperienceTable(Connection connection,
            PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "experience")) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "experience` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`taming` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`mining` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`woodcutting` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`repair` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`unarmed` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`herbalism` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`excavation` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`archery` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`swords` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`axes` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`acrobatics` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`fishing` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`alchemy` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`crossbows` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`tridents` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`maces` int(10) unsigned NOT NULL DEFAULT '0',"
                + "`spears` int(10) unsigned NOT NULL DEFAULT '0',"
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=" + CHARSET_SQL + ";";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.executeUpdate(sql);
        }
    }

    private boolean tableExists(PreparedStatement schemaStmt, String tableName)
            throws SQLException {
        setStatementQuery(schemaStmt, tableName);
        try (ResultSet rs = schemaStmt.executeQuery()) {
            return rs.next();
        }
    }

    private void truncateSkillsToCaps(Connection connection) throws SQLException {
        for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
            int cap = mcMMO.p.getSkillTools().getLevelCap(skill);
            if (cap == Integer.MAX_VALUE) {
                continue;
            }

            String column = skill.name().toLowerCase(Locale.ENGLISH);
            String sql = "UPDATE `" + tablePrefix + "skills` "
                    + "SET `" + column + "` = " + cap + " "
                    + "WHERE `" + column + "` > " + cap;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        }
    }

    private void deleteOrphans(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "experience` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "experience`.`user_id` = `u`.`id`)"
            );
            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "huds` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "huds`.`user_id` = `u`.`id`)"
            );
            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "cooldowns` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "cooldowns`.`user_id` = `u`.`id`)"
            );
            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "skills` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "skills`.`user_id` = `u`.`id`)"
            );
        }
    }

    private void updateStructure(String tableName, String columnName, String columnSize) {
        try (Connection connection = getConnection(PoolIdentifier.MISC)) {
            if (!columnExists(connection,
                    mcMMO.p.getGeneralConfig().getMySQLDatabaseName(),
                    tablePrefix + tableName,
                    columnName)) {

                try (Statement createStatement = connection.createStatement()) {
                    String startingLevel =
                            "'" + mcMMO.p.getAdvancedConfig().getStartingLevel() + "'";
                    String sql = "ALTER TABLE `" + tablePrefix + tableName + "` "
                            + "ADD COLUMN `" + columnName + "` int(" + columnSize + ") "
                            + "unsigned NOT NULL DEFAULT " + startingLevel;
                    createStatement.executeUpdate(sql);
                }
            }
        } catch (SQLException e) {
            logSQLException(e);
            throw new RuntimeException(e);
        }
    }

    private boolean columnExists(Connection connection,
            String database,
            String tableName,
            String columnName) throws SQLException {
        String sql = "SELECT `COLUMN_NAME` " +
                "FROM `INFORMATION_SCHEMA`.`COLUMNS` " +
                "WHERE `TABLE_SCHEMA`='" + database + "' " +
                "AND `TABLE_NAME`='" + tableName + "' " +
                "AND `COLUMN_NAME`='" + columnName + "'";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next();
        } catch (SQLException e) {
            logger.info("Failed to check if column exists in table " + tableName
                    + " for column " + columnName);
            logSQLException(e);
            throw e;
        }
    }

    private void setStatementQuery(PreparedStatement statement, String tableName)
            throws SQLException {
        statement.setString(1, mcMMO.p.getGeneralConfig().getMySQLDatabaseName());
        statement.setString(2, tablePrefix + tableName);
    }

    // ---------------------------------------------------------------------
    // Upgrade system
    // ---------------------------------------------------------------------

    Connection getConnection(PoolIdentifier identifier) throws SQLException {
        Connection connection = switch (identifier) {
            case LOAD -> loadPool.getConnection();
            case MISC -> miscPool.getConnection();
            case SAVE -> savePool.getConnection();
        };
        if (connection == null) {
            throw new RuntimeException(
                    "getConnection() for " + identifier.name().toLowerCase(Locale.ENGLISH)
                            + " pool timed out.  Increase max connections settings.");
        }
        return connection;
    }

    /**
     * Check database structure for necessary upgrades.
     *
     * @param upgrade Upgrade to attempt to apply
     */
    private void checkDatabaseStructure(Connection connection, UpgradeType upgrade) {
        if (!mcMMO.getUpgradeManager().shouldUpgrade(upgrade)) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            switch (upgrade) {
                case ADD_FISHING -> checkUpgradeAddFishing(statement);
                case ADD_BLAST_MINING_COOLDOWN -> checkUpgradeAddBlastMiningCooldown(statement);
                case ADD_MOB_HEALTHBARS -> checkUpgradeAddMobHealthbars(statement);
                case DROP_SQL_PARTY_NAMES -> checkUpgradeDropPartyNames(statement);
                case DROP_SPOUT -> checkUpgradeDropSpout(statement);
                case ADD_ALCHEMY -> checkUpgradeAddAlchemy(statement);
                case ADD_UUIDS -> {
                    checkUpgradeAddUUIDs(statement);
                }
                case ADD_SCOREBOARD_TIPS -> {
                    checkUpgradeAddScoreboardTips(statement);
                }
                case DROP_NAME_UNIQUENESS -> {
                    checkNameUniqueness(statement);
                }
                case ADD_SKILL_TOTAL -> checkUpgradeSkillTotal(connection);
                case ADD_UNIQUE_PLAYER_DATA -> checkUpgradeAddUniqueChimaeraWing(statement);
                case SQL_CHARSET_UTF8MB4 -> updateCharacterSet(statement);
                default -> {
                    // no-op
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void writeMissingRows(Connection connection, int id) {
        String expSql = "INSERT IGNORE INTO " + tablePrefix + "experience (user_id) VALUES (?)";
        String skillsSql = "INSERT IGNORE INTO " + tablePrefix + "skills (user_id) VALUES (?)";
        String cooldownsSql =
                "INSERT IGNORE INTO " + tablePrefix + "cooldowns (user_id) VALUES (?)";
        String hudsSql = "INSERT IGNORE INTO " + tablePrefix
                + "huds (user_id, mobhealthbar, scoreboardtips) VALUES (?, ?, ?)";

        try (PreparedStatement expStmt = connection.prepareStatement(expSql);
                PreparedStatement skillsStmt = connection.prepareStatement(skillsSql);
                PreparedStatement cdStmt = connection.prepareStatement(cooldownsSql);
                PreparedStatement hudStmt = connection.prepareStatement(hudsSql)) {

            expStmt.setInt(1, id);
            expStmt.execute();

            skillsStmt.setInt(1, id);
            skillsStmt.execute();

            cdStmt.setInt(1, id);
            cdStmt.execute();

            hudStmt.setInt(1, id);
            hudStmt.setString(2, mcMMO.p.getGeneralConfig().getMobHealthbarDefault().name());
            hudStmt.setInt(3, 0);
            hudStmt.execute();
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void checkNameUniqueness(final Statement statement) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SHOW INDEXES "
                    + "FROM `" + tablePrefix + "users` "
                    + "WHERE Column_name='user' "
                    + " AND NOT Non_unique");
            if (!resultSet.next()) {
                return;
            }
            resultSet.close();
            logger.info("Updating mcMMO MySQL tables to drop name uniqueness...");
            statement.execute("ALTER TABLE `" + tablePrefix + "users` "
                    + "DROP INDEX `user`,"
                    + "ADD INDEX `user` (`user`(20) ASC)");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.DROP_NAME_UNIQUENESS);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
        }
    }

    private void checkUpgradeAddAlchemy(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `alchemy` FROM `" + tablePrefix + "skills` LIMIT 1");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_ALCHEMY);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for Alchemy...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "skills` ADD `alchemy` int(10) NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "experience` ADD `alchemy` int(10) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddBlastMiningCooldown(final Statement statement) throws SQLException {
        try {
            statement.executeQuery(
                    "SELECT `blast_mining` FROM `" + tablePrefix + "cooldowns` LIMIT 1");
            mcMMO.getUpgradeManager()
                    .setUpgradeCompleted(UpgradeType.ADD_BLAST_MINING_COOLDOWN);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for Blast Mining...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "cooldowns` ADD `blast_mining` int(32) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddUniqueChimaeraWing(final Statement statement)
            throws SQLException {
        try {
            statement.executeQuery(
                    "SELECT `chimaera_wing` FROM `" + tablePrefix + "cooldowns` LIMIT 1");
            mcMMO.getUpgradeManager()
                    .setUpgradeCompleted(UpgradeType.ADD_UNIQUE_PLAYER_DATA);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for Chimaera Wing...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "cooldowns` ADD `chimaera_wing` int(32) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddFishing(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `fishing` FROM `" + tablePrefix + "skills` LIMIT 1");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_FISHING);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for Fishing...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "skills` ADD `fishing` int(10) NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "experience` ADD `fishing` int(10) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddMobHealthbars(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `mobhealthbar` FROM `" + tablePrefix + "huds` LIMIT 1");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_MOB_HEALTHBARS);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for mob healthbars...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "huds` ADD `mobhealthbar` varchar(50) NOT NULL DEFAULT '"
                    + mcMMO.p.getGeneralConfig().getMobHealthbarDefault() + "'");
        }
    }

    private void checkUpgradeAddScoreboardTips(final Statement statement) throws SQLException {
        try {
            statement.executeQuery(
                    "SELECT `scoreboardtips` FROM `" + tablePrefix + "huds` LIMIT 1");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_SCOREBOARD_TIPS);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for scoreboard tips...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "huds` ADD `scoreboardtips` int(10) NOT NULL DEFAULT '0' ;");
        }
    }

    private void checkUpgradeAddUUIDs(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "users` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean columnExists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("uuid")) {
                    columnExists = true;
                    break;
                }
            }

            if (!columnExists) {
                logger.info("Adding UUIDs to mcMMO MySQL user table...");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix
                        + "users` ADD `uuid` varchar(36) NULL DEFAULT NULL");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix
                        + "users` ADD UNIQUE INDEX `uuid` (`uuid`) USING BTREE");

                mcMMO.p.getFoliaLib().getScheduler().runLaterAsync(new GetUUIDUpdatesRequired(),
                        100); // wait until after first purge
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
        }
    }

    private void checkUpgradeDropPartyNames(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "users` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean columnExists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("party")) {
                    columnExists = true;
                    break;
                }
            }

            if (columnExists) {
                logger.info("Removing party name from users table...");
                statement.executeUpdate(
                        "ALTER TABLE `" + tablePrefix + "users` DROP COLUMN `party`");
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.DROP_SQL_PARTY_NAMES);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
        }
    }

    private void checkUpgradeSkillTotal(final Connection connection) throws SQLException {
        ResultSet resultSet = null;
        Statement statement = null;

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "skills` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean columnExists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("total")) {
                    columnExists = true;
                    break;
                }
            }

            if (!columnExists) {
                logger.info("Adding skill total column to skills table...");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix
                        + "skills` ADD COLUMN `total` int NOT NULL DEFAULT '0'");
                statement.executeUpdate("UPDATE `" + tablePrefix
                        + "skills` SET `total` = (taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing+alchemy)");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix
                        + "skills` ADD INDEX `idx_total` (`total`) USING BTREE");
                connection.commit();
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_SKILL_TOTAL);
        } catch (SQLException ex) {
            logSQLException(ex);
            try {
                connection.rollback();
            } catch (SQLException ignored) {
                // best effort
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            tryClose(resultSet);
            tryClose(statement);
        }
    }


    private void checkUpgradeDropSpout(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "huds` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean columnExists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("hudtype")) {
                    columnExists = true;
                    break;
                }
            }

            if (columnExists) {
                logger.info("Removing Spout HUD type from huds table...");
                statement.executeUpdate(
                        "ALTER TABLE `" + tablePrefix + "huds` DROP COLUMN `hudtype`");
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.DROP_SPOUT);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
        }
    }

    private int getUserID(final Connection connection,
            final String playerName,
            final UUID uuid) {
        if (uuid == null) {
            return getUserIDByName(connection, playerName);
        }

        Integer cached = cachedUserIDs.get(uuid);
        if (cached != null) {
            return cached;
        }

        String sql = "SELECT id, `user` FROM " + tablePrefix
                + "users WHERE uuid = ? OR (uuid IS NULL AND `user` = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    cachedUserIDs.put(uuid, id);
                    return id;
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        return -1;
    }

    private int getUserIDByName(final Connection connection, final String playerName) {
        String sql = "SELECT id, `user` FROM " + tablePrefix + "users WHERE `user` = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }

        return -1;
    }

    private void tryClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // Ignore
            }
        }
    }

    @Override
    public void onDisable() {
        LogUtils.debug(logger, "Releasing connection pool resource...");
        miscPool.close();
        loadPool.close();
        savePool.close();
    }

    public void resetMobHealthSettings() {
        String sql = "UPDATE " + tablePrefix + "huds SET mobhealthbar = ?";

        try (Connection connection = getConnection(PoolIdentifier.MISC);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1,
                    mcMMO.p.getGeneralConfig().getMobHealthbarDefault().toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void updateCharacterSet(@NotNull Statement statement) {
        logger.info("SQL Converting tables from latin1 to utf8mb4");

        try {
            logger.info("Updating user column to new encoding");
            statement.executeUpdate(getUpdateUserInUsersTableSQLQuery());

            logger.info("Updating user column to new encoding");
            statement.executeUpdate(getUpdateUUIDInUsersTableSQLQuery());

            logger.info("Updating mobhealthbar column to new encoding");
            statement.executeUpdate(getUpdateMobHealthBarInHudsTableSQLQuery());

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.SQL_CHARSET_UTF8MB4);
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    @NotNull
    private String getUpdateUserInUsersTableSQLQuery() {
        return "ALTER TABLE\n" +
                "    " + tablePrefix + "users\n" +
                "    CHANGE `user` user\n" +
                "    " + USER_VARCHAR + "\n" +
                "    CHARACTER SET utf8mb4\n" +
                "    COLLATE utf8mb4_unicode_ci;";
    }

    @NotNull
    private String getUpdateUUIDInUsersTableSQLQuery() {
        return "ALTER TABLE\n" +
                "    " + tablePrefix + "users\n" +
                "    CHANGE uuid uuid\n" +
                "    " + UUID_VARCHAR + "\n" +
                "    CHARACTER SET utf8mb4\n" +
                "    COLLATE utf8mb4_unicode_ci;";
    }

    @NotNull
    private String getUpdateMobHealthBarInHudsTableSQLQuery() {
        return "ALTER TABLE\n" +
                "    " + tablePrefix + "huds\n" +
                "    CHANGE mobhealthbar mobhealthbar\n" +
                "    " + MOBHEALTHBAR_VARCHAR + "\n" +
                "    CHARACTER SET utf8mb4\n" +
                "    COLLATE utf8mb4_unicode_ci;";
    }

    private void logSQLException(SQLException ex) {
        SQLException current = ex;

        while (current != null) {
            logger.severe("SQLException occurred:");
            logger.severe("  Message:    " + current.getMessage());
            logger.severe("  SQLState:   " + current.getSQLState());
            logger.severe("  VendorCode: " + current.getErrorCode());

            StringWriter sw = new StringWriter();
            current.printStackTrace(new PrintWriter(sw));
            logger.severe(sw.toString());

            current = current.getNextException();
            if (current != null) {
                logger.severe("Caused by next SQLException in chain:");
            }
        }
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.SQL;
    }

    public enum PoolIdentifier {
        MISC,
        LOAD,
        SAVE
    }

    private class GetUUIDUpdatesRequired implements Runnable {
        @Override
        public void run() {
            massUpdateLock.lock();
            List<String> names = new ArrayList<>();
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                try {
                    connection = miscPool.getConnection();
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(
                            "SELECT `user` FROM `" + tablePrefix + "users` WHERE `uuid` IS NULL");

                    while (resultSet.next()) {
                        names.add(resultSet.getString("user"));
                    }
                } catch (SQLException ex) {
                    logSQLException(ex);
                } finally {
                    tryClose(resultSet);
                    tryClose(statement);
                    tryClose(connection);
                }

                if (!names.isEmpty()) {
                    UUIDUpdateAsyncTask updateTask = new UUIDUpdateAsyncTask(mcMMO.p, names);
                    updateTask.start();
                    updateTask.waitUntilFinished();
                }
            } finally {
                massUpdateLock.unlock();
            }
        }
    }
}
