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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SQLDatabaseManager implements DatabaseManager {
    private static final String ALL_QUERY_VERSION = "total";
    public static final String MOBHEALTHBAR_VARCHAR = "VARCHAR(50)";
    public static final String UUID_VARCHAR = "VARCHAR(36)";
    public static final String USER_VARCHAR = "VARCHAR(40)";
    public static final int CHILD_SKILLS_SIZE = 2;
    public static final String LEGACY_DRIVER_PATH = "com.mysql.jdbc.Driver";
    private final String tablePrefix = mcMMO.p.getGeneralConfig().getMySQLTablePrefix();

    private final Map<UUID, Integer> cachedUserIDs = new HashMap<>();

    private DataSource miscPool;
    private DataSource loadPool;
    private DataSource savePool;

    private final ReentrantLock massUpdateLock = new ReentrantLock();

    private final String CHARSET_SQL = "utf8mb4"; //This is compliant with UTF-8 while "utf8" is not, confusing but this is how it is.
    private final Logger logger;

    SQLDatabaseManager(Logger logger, String driverPath) {
        this.logger = logger;
        String connectionString = getConnectionString();

        if (mcMMO.p.getGeneralConfig().getMySQLPublicKeyRetrieval()) {
            connectionString +=
                    "&allowPublicKeyRetrieval=true";
        }

        try {
            // Force driver to load if not yet loaded
            Class.forName(driverPath);
        } catch (ClassNotFoundException e) {
            try {
                driverPath = LEGACY_DRIVER_PATH; //fall on deprecated path if new path isn't found
                Class.forName(driverPath);
            } catch (ClassNotFoundException ex) {
                e.printStackTrace();
                ex.printStackTrace();
                logger.severe("Neither driver found");
                return;
            }
            //throw e; // aborts onEnable()  Riking if you want to do this, fully implement it.
        }

        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(driverPath);
        poolProperties.setUrl(connectionString);
        poolProperties.setUsername(mcMMO.p.getGeneralConfig().getMySQLUserName());
        poolProperties.setPassword(mcMMO.p.getGeneralConfig().getMySQLUserPassword());
        poolProperties.setMaxIdle(
                mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(PoolIdentifier.MISC));
        poolProperties.setMaxActive(
                mcMMO.p.getGeneralConfig().getMySQLMaxConnections(PoolIdentifier.MISC));
        poolProperties.setInitialSize(0);
        poolProperties.setMaxWait(-1);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setValidationInterval(30000);
        miscPool = new DataSource(poolProperties);
        poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(driverPath);
        poolProperties.setUrl(connectionString);
        poolProperties.setUsername(mcMMO.p.getGeneralConfig().getMySQLUserName());
        poolProperties.setPassword(mcMMO.p.getGeneralConfig().getMySQLUserPassword());
        poolProperties.setInitialSize(0);
        poolProperties.setMaxIdle(
                mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(PoolIdentifier.SAVE));
        poolProperties.setMaxActive(
                mcMMO.p.getGeneralConfig().getMySQLMaxConnections(PoolIdentifier.SAVE));
        poolProperties.setMaxWait(-1);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setValidationInterval(30000);
        savePool = new DataSource(poolProperties);
        poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(driverPath);
        poolProperties.setUrl(connectionString);
        poolProperties.setUsername(mcMMO.p.getGeneralConfig().getMySQLUserName());
        poolProperties.setPassword(mcMMO.p.getGeneralConfig().getMySQLUserPassword());
        poolProperties.setInitialSize(0);
        poolProperties.setMaxIdle(
                mcMMO.p.getGeneralConfig().getMySQLMaxPoolSize(PoolIdentifier.LOAD));
        poolProperties.setMaxActive(
                mcMMO.p.getGeneralConfig().getMySQLMaxConnections(PoolIdentifier.LOAD));
        poolProperties.setMaxWait(-1);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setValidationInterval(30000);
        loadPool = new DataSource(poolProperties);

        checkStructure();
    }

    @NotNull
    private static String getConnectionString() {
        String connectionString = "jdbc:mysql://" + mcMMO.p.getGeneralConfig().getMySQLServerName()
                + ":" + mcMMO.p.getGeneralConfig().getMySQLServerPort() + "/"
                + mcMMO.p.getGeneralConfig().getMySQLDatabaseName();

        if (!mcMMO.getCompatibilityManager().getMinecraftGameVersion().isAtLeast(1, 17, 0)
                //Temporary hack for SQL and 1.17 support
                && mcMMO.p.getGeneralConfig().getMySQLSSL()) {
            connectionString +=
                    "?verifyServerCertificate=false" +
                            "&useSSL=true" +
                            "&requireSSL=true";
        } else {
            connectionString +=
                    "?useSSL=false";
        }
        return connectionString;
    }

    // TODO: unit tests
    public int purgePowerlessUsers() {
        massUpdateLock.lock();
        logger.info("Purging powerless users...");

        Connection connection = null;
        Statement statement = null;
        int purged = 0;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.createStatement();

            purged = statement.executeUpdate("DELETE FROM " + tablePrefix + "skills WHERE "
                    + "taming = 0 AND mining = 0 AND woodcutting = 0 AND repair = 0 "
                    + "AND unarmed = 0 AND herbalism = 0 AND excavation = 0 AND "
                    + "archery = 0 AND swords = 0 AND axes = 0 AND acrobatics = 0 "
                    + "AND fishing = 0 AND alchemy = 0 AND crossbows = 0 AND tridents = 0 AND maces = 0 AND spears = 0;");

            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "experience` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "experience`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "huds` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "huds`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "cooldowns` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "cooldowns`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "users` WHERE NOT EXISTS (SELECT * FROM `"
                            + tablePrefix + "skills` `s` WHERE `" + tablePrefix
                            + "users`.`id` = `s`.`user_id`)");
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
            tryClose(connection);
            massUpdateLock.unlock();
        }

        logger.info("Purged " + purged + " users from the database.");
        return purged;
    }

    public void purgeOldUsers() {
        massUpdateLock.lock();
        logger.info("Purging inactive users older than " + (mcMMO.p.getPurgeTime() / 2630000000L)
                + " months...");

        Connection connection = null;
        Statement statement = null;
        int purged = 0;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.createStatement();

            purged = statement.executeUpdate(
                    "DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                            "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                            "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                            "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                            "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                            "WHERE ((UNIX_TIMESTAMP() - lastlogin) > " + mcMMO.p.getPurgeTime()
                            + ")");
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
            tryClose(connection);
            massUpdateLock.unlock();
        }

        logger.info("Purged " + purged + " users from the database.");
    }

    public boolean removeUser(String playerName, UUID uuid) {
        boolean success = false;
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement("DELETE FROM u, e, h, s, c " +
                    "USING " + tablePrefix + "users u " +
                    "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                    "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                    "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                    "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                    "WHERE u.`user` = ?");

            statement.setString(1, playerName);

            success = statement.executeUpdate() != 0;
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
            tryClose(connection);
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

    public boolean saveUser(PlayerProfile profile) {
        final String playerName = profile.getPlayerName();
        final UUID uuid = profile.getUniqueId();

        try (Connection connection = getConnection(PoolIdentifier.SAVE)) {
            // Make the whole save atomic
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

                if (!updateLastLogin(connection, userId, playerName)) {
                    connection.rollback();
                    return false;
                }

                if (!updateSkills(connection, userId, profile, playerName)) {
                    connection.rollback();
                    return false;
                }

                if (!updateExperience(connection, userId, profile, playerName)) {
                    connection.rollback();
                    return false;
                }

                if (!updateCooldowns(connection, userId, profile, playerName)) {
                    connection.rollback();
                    return false;
                }

                if (!updateHudSettings(connection, userId, profile, playerName)) {
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
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        }
    }

    private boolean updateLastLogin(Connection connection, int userId, String playerName) {
        String sql = "UPDATE " + tablePrefix + "users SET lastlogin = UNIX_TIMESTAMP() WHERE id = ?";

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

    private boolean updateSkills(Connection connection, int userId, PlayerProfile profile, String playerName) {
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

    private boolean updateExperience(Connection connection, int userId, PlayerProfile profile, String playerName) {
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

    private boolean updateCooldowns(Connection connection, int userId, PlayerProfile profile, String playerName) {
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

    private boolean updateHudSettings(Connection connection, int userId, PlayerProfile profile, String playerName) {
        String sql = "UPDATE " + tablePrefix + "huds SET mobhealthbar = ?, scoreboardtips = ? WHERE user_id = ?";

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


    public @NotNull List<PlayerStat> readLeaderboard(@Nullable PrimarySkillType skill,
            int pageNumber, int statsPerPage) throws InvalidSkillException {
        List<PlayerStat> stats = new ArrayList<>();

        //Fix for a plugin that people are using that is throwing SQL errors
        if (skill != null && SkillTools.isChildSkill(skill)) {
            logger.severe(
                    "A plugin hooking into mcMMO is being naughty with our database commands, update all plugins that hook into mcMMO and contact their devs!");
            throw new InvalidSkillException(
                    "A plugin hooking into mcMMO that you are using is attempting to read leaderboard skills for child skills, child skills do not have leaderboards! This is NOT an mcMMO error!");
        }

        String query = skill == null ? ALL_QUERY_VERSION : skill.name().toLowerCase(Locale.ENGLISH);
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement(
                    "SELECT " + query + ", `user` FROM " + tablePrefix + "users JOIN " + tablePrefix
                            + "skills ON (user_id = id) WHERE " + query
                            + " > 0 AND NOT `user` = '\\_INVALID\\_OLD\\_USERNAME\\_' ORDER BY "
                            + query + " DESC, `user` LIMIT ?, ?");
            statement.setInt(1, (pageNumber * statsPerPage) - statsPerPage);
            statement.setInt(2, statsPerPage);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ArrayList<String> column = new ArrayList<>();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    column.add(resultSet.getString(i));
                }

                stats.add(new PlayerStat(column.get(1), Integer.parseInt(column.get(0))));
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
            tryClose(statement);
            tryClose(connection);
        }

        return stats;
    }

    public Map<PrimarySkillType, Integer> readRank(String playerName) {
        Map<PrimarySkillType, Integer> skills = new HashMap<>();

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            for (PrimarySkillType primarySkillType : SkillTools.NON_CHILD_SKILLS) {
                String skillName = primarySkillType.name().toLowerCase(Locale.ENGLISH);
                // Get count of all users with higher skill level than player
                String sql = "SELECT COUNT(*) AS 'rank' FROM " + tablePrefix + "users JOIN "
                        + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                        "AND " + skillName + " > (SELECT " + skillName + " FROM " + tablePrefix
                        + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                        "WHERE `user` = ?)";

                statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                resultSet = statement.executeQuery();

                resultSet.next();

                int rank = resultSet.getInt("rank");

                // Ties are settled by alphabetical order
                sql = "SELECT user, " + skillName + " FROM " + tablePrefix + "users JOIN "
                        + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                        "AND " + skillName + " = (SELECT " + skillName + " FROM " + tablePrefix
                        + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                        "WHERE `user` = '" + playerName + "') ORDER BY user";

                resultSet.close();
                statement.close();

                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getString("user").equalsIgnoreCase(playerName)) {
                        skills.put(primarySkillType, rank + resultSet.getRow());
                        break;
                    }
                }

                resultSet.close();
                statement.close();
            }

            String sql =
                    "SELECT COUNT(*) AS 'rank' FROM " + tablePrefix + "users JOIN " + tablePrefix
                            + "skills ON user_id = id " +
                            "WHERE " + ALL_QUERY_VERSION + " > 0 " +
                            "AND " + ALL_QUERY_VERSION + " > " +
                            "(SELECT " + ALL_QUERY_VERSION + " " +
                            "FROM " + tablePrefix + "users JOIN " + tablePrefix
                            + "skills ON user_id = id WHERE `user` = ?)";

            statement = connection.prepareStatement(sql);
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            resultSet.next();

            int rank = resultSet.getInt("rank");

            resultSet.close();
            statement.close();

            sql = "SELECT user, " + ALL_QUERY_VERSION + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id "
                    +
                    "WHERE " + ALL_QUERY_VERSION + " > 0 " +
                    "AND " + ALL_QUERY_VERSION + " = " +
                    "(SELECT " + ALL_QUERY_VERSION + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix
                    + "skills ON user_id = id WHERE `user` = ?) ORDER BY user";

            statement = connection.prepareStatement(sql);
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("user").equalsIgnoreCase(playerName)) {
                    skills.put(null, rank + resultSet.getRow());
                    break;
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
            tryClose(statement);
            tryClose(connection);
        }

        return skills;
    }

    public @NotNull PlayerProfile newUser(String playerName, UUID uuid) {
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            newUser(connection, playerName, uuid);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(connection);
        }

        return new PlayerProfile(playerName, uuid, true,
                mcMMO.p.getAdvancedConfig().getStartingLevel());
    }

    @Override
    public @NotNull PlayerProfile newUser(@NotNull Player player) {
        try {
            Connection connection = getConnection(PoolIdentifier.SAVE);
            int id = newUser(connection, player.getName(), player.getUniqueId());

            if (id == -1) {
                return new PlayerProfile(player.getName(), player.getUniqueId(), false,
                        mcMMO.p.getAdvancedConfig().getStartingLevel());
            } else {
                return loadPlayerProfile(player);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unexpected SQLException while creating new user for "
                    + player.getName(), e);
        }

        return new PlayerProfile(player.getName(), player.getUniqueId(), false,
                mcMMO.p.getAdvancedConfig().getStartingLevel());
    }

    private static final String INVALID_OLD_USERNAME = "_INVALID_OLD_USERNAME_";

    private int newUser(Connection connection, String playerName, @Nullable UUID uuid) {
        if (connection == null) {
            throw new IllegalArgumentException("connection must not be null");
        }
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
                    logger.severe("Unable to create new user account in DB for player '" + playerName + "'");
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
            return new PlayerProfile(playerName, false,
                    mcMMO.p.getAdvancedConfig().getStartingLevel());
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
                    "Error looking up player, both UUID and playerName are null and one must not be.");
        }

        try (Connection connection = getConnection(PoolIdentifier.LOAD)) {
            int id = getUserID(connection, playerName, uuid);

            if (id == -1) {
                return createEmptyProfile(playerName);
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
                            // c.taming is unused for abilities, left out or alias if you want it
                            "c.mining        AS cd_super_breaker, " +
                            "c.repair        AS cd_repair_unused, " +     // unused but explicit
                            "c.woodcutting   AS cd_tree_feller, " +
                            "c.unarmed       AS cd_berserk, " +
                            "c.herbalism     AS cd_green_terra, " +
                            "c.excavation    AS cd_giga_drill_breaker, " +
                            "c.archery       AS cd_explosive_shot, " +
                            "c.swords        AS cd_serrated_strikes, " +
                            "c.axes          AS cd_skull_splitter, " +
                            "c.acrobatics    AS cd_acrobatics_unused, " + // unused but explicit
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
                        return createEmptyProfile(playerName);
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
            return createEmptyProfile(playerName);
        }
    }


    private PlayerProfile createEmptyProfile(@Nullable String playerName) {
        return new PlayerProfile(playerName, mcMMO.p.getAdvancedConfig().getStartingLevel());
    }

    private boolean shouldUpdateUsername(@Nullable String playerName, @Nullable UUID uuid, String nameInDb) {
        return playerName != null
                && !playerName.isEmpty()
                && !playerName.equalsIgnoreCase(nameInDb)
                && uuid != null;
    }

    private void invalidateOldUsername(Connection connection, String oldName) throws SQLException {
        String sql = "UPDATE `" + tablePrefix + "users` SET `user` = ? WHERE `user` = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "_INVALID_OLD_USERNAME_");
            stmt.setString(2, oldName);
            stmt.executeUpdate();
        }
    }

    private void updateCurrentUsername(Connection connection, int id, String playerName, UUID uuid) throws SQLException {
        String sql = "UPDATE `" + tablePrefix + "users` SET `user` = ?, uuid = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, uuid.toString());
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }


    public void convertUsers(DatabaseManager destination) {
            // Get the list of usernames we want to migrate
            final List<String> usernames = getStoredUsers();
            if (usernames.isEmpty()) {
                logger.info("No stored users found to convert.");
                return;
            }

            int convertedUsers = 0;
            long startMillis = System.currentTimeMillis();

            for (String playerName : usernames) {
                try {
                    // Reuse the canonical loading path (handles schema, aliases, etc.)
                    final PlayerProfile profile = loadPlayerProfile(playerName);

                    // Delegate save to the destination database manager
                    destination.saveUser(profile);
                } catch (Exception ex) {
                    // Log and continue with remaining users
                    logger.log(Level.SEVERE, "Failed to convert user '" + playerName + "'", ex);
                }

                convertedUsers++;
                Misc.printProgress(convertedUsers, progressInterval, startMillis);
            }

            logger.info("Finished converting " + convertedUsers + " users.");
        }

    public boolean saveUserUUID(String userName, UUID uuid) {
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement(
                    "UPDATE `" + tablePrefix + "users` SET "
                            + "  uuid = ? WHERE `user` = ?");
            statement.setString(1, uuid.toString());
            statement.setString(2, userName);
            statement.execute();
            return true;
        } catch (SQLException ex) {
            logSQLException(ex);
            return false;
        } finally {
            tryClose(statement);
            tryClose(connection);
        }
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        PreparedStatement statement = null;
        int count = 0;

        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement(
                    "UPDATE " + tablePrefix + "users SET uuid = ? WHERE `user` = ?");

            for (Map.Entry<String, UUID> entry : fetchedUUIDs.entrySet()) {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey());

                statement.addBatch();

                count++;

                if ((count % 500) == 0) {
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
        } finally {
            tryClose(statement);
            tryClose(connection);
        }
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<>();

        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT `user` FROM " + tablePrefix + "users");
            while (resultSet.next()) {
                users.add(resultSet.getString("user"));
            }
        } catch (SQLException e) {
            logSQLException(e);
        } finally {
            tryClose(resultSet);
            tryClose(statement);
            tryClose(connection);
        }

        return users;
    }

    /**
     * Checks that the database structure is present and correct
     */
    /**
     * Checks that the database structure is present and correct.
     * Runs once on startup.
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

    private void ensureUsersTable(Connection connection, PreparedStatement schemaStmt) throws SQLException {
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

    private void ensureHudsTable(Connection connection, PreparedStatement schemaStmt) throws SQLException {
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

    private void ensureCooldownsTable(Connection connection, PreparedStatement schemaStmt) throws SQLException {
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

    private void ensureSkillsTable(Connection connection, PreparedStatement schemaStmt) throws SQLException {
        if (tableExists(schemaStmt, "skills")) {
            return;
        }

        int starting = mcMMO.p.getAdvancedConfig().getStartingLevel();
        String startingLevel = "'" + starting + "'";
        String totalLevel = "'" + (starting * (PrimarySkillType.values().length - CHILD_SKILLS_SIZE)) + "'";

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

    private void ensureExperienceTable(Connection connection, PreparedStatement schemaStmt) throws SQLException {
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

    /**
     * Uses the shared schema PreparedStatement + existing setStatementQuery logic
     * to determine if a given logical table exists.
     */
    private boolean tableExists(PreparedStatement schemaStmt, String tableName) throws SQLException {
        setStatementQuery(schemaStmt, tableName);
        try (ResultSet rs = schemaStmt.executeQuery()) {
            return rs.next();
        }
    }

    /* -------------------- Post-creation maintenance -------------------- */

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
        LogUtils.debug(logger, "Killing orphans");

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "experience` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "experience`.`user_id` = `u`.`id`)");

            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "huds` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "huds`.`user_id` = `u`.`id`)");

            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "cooldowns` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "cooldowns`.`user_id` = `u`.`id`)");

            stmt.executeUpdate(
                    "DELETE FROM `" + tablePrefix + "skills` " +
                            "WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` " +
                            "WHERE `" + tablePrefix + "skills`.`user_id` = `u`.`id`)");
        }
    }

    /* -------------------- Existing helpers (lightly cleaned) -------------------- */

    private void updateStructure(String tableName, String columnName, String columnSize) {
        try (Connection connection = getConnection(PoolIdentifier.MISC)) {
            if (!columnExists(connection,
                    mcMMO.p.getGeneralConfig().getMySQLDatabaseName(),
                    tablePrefix + tableName,
                    columnName)) {

                try (Statement createStatement = connection.createStatement()) {
                    String startingLevel = "'" + mcMMO.p.getAdvancedConfig().getStartingLevel() + "'";
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

    private boolean columnExists(Connection connection, String database, String tableName,
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
        // Set schema name for MySQL
        statement.setString(1, mcMMO.p.getGeneralConfig().getMySQLDatabaseName());
        statement.setString(2, tablePrefix + tableName);
    }

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
        // TODO: Rewrite / Refactor
        if (!mcMMO.getUpgradeManager().shouldUpgrade(upgrade)) {
            LogUtils.debug(logger, "Skipping " + upgrade.name() + " upgrade (unneeded)");
            return;
        }

        Statement statement = null;

        try {
            statement = connection.createStatement();

            switch (upgrade) {
                case ADD_FISHING:
                    checkUpgradeAddFishing(statement);
                    break;

                case ADD_BLAST_MINING_COOLDOWN:
                    checkUpgradeAddBlastMiningCooldown(statement);
                    break;

                case ADD_SQL_INDEXES:
//                    checkUpgradeAddSQLIndexes(statement);
                    break;

                case ADD_MOB_HEALTHBARS:
                    checkUpgradeAddMobHealthbars(statement);
                    break;

                case DROP_SQL_PARTY_NAMES:
                    checkUpgradeDropPartyNames(statement);
                    break;

                case DROP_SPOUT:
                    checkUpgradeDropSpout(statement);
                    break;

                case ADD_ALCHEMY:
                    checkUpgradeAddAlchemy(statement);
                    break;

                case ADD_UUIDS:
                    checkUpgradeAddUUIDs(statement);
                    return;

                case ADD_SCOREBOARD_TIPS:
                    checkUpgradeAddScoreboardTips(statement);
                    return;

                case DROP_NAME_UNIQUENESS:
                    checkNameUniqueness(statement);
                    return;

                case ADD_SKILL_TOTAL:
                    checkUpgradeSkillTotal(connection);
                    break;
                case ADD_UNIQUE_PLAYER_DATA:
                    checkUpgradeAddUniqueChimaeraWing(statement);
                    break;

                case SQL_CHARSET_UTF8MB4:
                    updateCharacterSet(statement);
                    break;

                default:
                    break;

            }
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
        }
    }

    private void writeMissingRows(Connection connection, int id) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(
                    "INSERT IGNORE INTO " + tablePrefix + "experience (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement(
                    "INSERT IGNORE INTO " + tablePrefix + "skills (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement(
                    "INSERT IGNORE INTO " + tablePrefix + "cooldowns (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix
                    + "huds (user_id, mobhealthbar, scoreboardtips) VALUES (?, ?, ?)");
            statement.setInt(1, id);
            statement.setString(2, mcMMO.p.getGeneralConfig().getMobHealthbarDefault().name());
            statement.setInt(3, 0);
            statement.execute();
            statement.close();
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
        }
    }

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

    private PlayerProfile loadFromResult(String playerName, ResultSet result) throws SQLException {
        final var skills = new EnumMap<PrimarySkillType, Integer>(PrimarySkillType.class);
        final var skillsXp = new EnumMap<PrimarySkillType, Float>(PrimarySkillType.class);
        final var skillsDATS = new EnumMap<SuperAbilityType, Integer>(SuperAbilityType.class);
        final var uniqueData = new EnumMap<UniqueDataType, Integer>(UniqueDataType.class);

        // --- Skills & XP by predictable alias name ---

        for (PrimarySkillType skill : PERSISTED_SKILLS) {
            String base = skill.name().toLowerCase(Locale.ROOT); // e.g. "taming", "woodcutting"

            int level  = result.getInt("skill_" + base);
            float xp   = result.getFloat("xp_" + base);

            skills.put(skill, level);
            skillsXp.put(skill, xp);
        }

        // --- Cooldowns / DATS ---

        skillsDATS.put(SuperAbilityType.SUPER_BREAKER,
                result.getInt("cd_super_breaker"));
        // cd_repair_unused exists but is not mapped to an ability
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
        // cd_acrobatics_unused exists but not mapped
        skillsDATS.put(SuperAbilityType.BLAST_MINING,
                result.getInt("cd_blast_mining"));

        uniqueData.put(UniqueDataType.CHIMAERA_WING_DATS,
                result.getInt("ud_chimaera_wing_dats"));

        skillsDATS.put(SuperAbilityType.SUPER_SHOTGUN,
                result.getInt("cd_super_shotgun"));
        skillsDATS.put(SuperAbilityType.TRIDENTS_SUPER_ABILITY,
                result.getInt("cd_tridents_super_ability"));
        skillsDATS.put(SuperAbilityType.MACES_SUPER_ABILITY,
                result.getInt("cd_maces_super_ability"));
        skillsDATS.put(SuperAbilityType.SPEARS_SUPER_ABILITY,
                result.getInt("cd_spears_super_ability"));

        // --- HUD + UUID ---

        int scoreboardTipsShown;
        try {
            // For older schemas this may not exist; keep your defensive behavior.
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
            // Keep uuid as null
        }

        return new PlayerProfile(playerName, uuid, skills, skillsXp, skillsDATS,
                scoreboardTipsShown, uniqueData, null);
    }


    private void logSQLException(SQLException ex) {
        SQLException current = ex;

        while (current != null) {
            logger.severe("SQLException occurred:");
            logger.severe("  Message:    " + current.getMessage());
            logger.severe("  SQLState:   " + current.getSQLState());
            logger.severe("  VendorCode: " + current.getErrorCode());

            // Log the full stack trace
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
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_BLAST_MINING_COOLDOWN);
        } catch (SQLException ex) {
            logger.info("Updating mcMMO MySQL tables for Blast Mining...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix
                    + "cooldowns` ADD `blast_mining` int(32) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddUniqueChimaeraWing(final Statement statement) throws SQLException {
        try {
            statement.executeQuery(
                    "SELECT `chimaera_wing` FROM `" + tablePrefix + "cooldowns` LIMIT 1");
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UNIQUE_PLAYER_DATA);
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

    private void checkUpgradeAddSQLIndexes(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery(
                    "SHOW INDEX FROM `" + tablePrefix + "skills` WHERE `Key_name` LIKE 'idx\\_%'");
            resultSet.last();

            if (resultSet.getRow() != SkillTools.NON_CHILD_SKILLS.size()) {
                logger.info("Indexing tables, this may take a while on larger databases");

                for (PrimarySkillType skill : SkillTools.NON_CHILD_SKILLS) {
                    String skill_name = skill.name().toLowerCase(Locale.ENGLISH);

                    try {
                        statement.executeUpdate(
                                "ALTER TABLE `" + tablePrefix + "skills` ADD INDEX `idx_"
                                        + skill_name + "` (`" + skill_name + "`) USING BTREE");
                    } catch (SQLException ex) {
                        // Ignore
                    }
                }
            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_SQL_INDEXES);
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
        }
    }

    private void checkUpgradeAddUUIDs(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "users` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean column_exists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("uuid")) {
                    column_exists = true;
                    break;
                }
            }

            if (!column_exists) {
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

    private class GetUUIDUpdatesRequired implements Runnable {
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

    private void checkUpgradeDropPartyNames(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "users` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean column_exists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("party")) {
                    column_exists = true;
                    break;
                }
            }

            if (column_exists) {
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
            boolean column_exists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("total")) {
                    column_exists = true;
                    break;
                }
            }

            if (!column_exists) {
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
        } finally {
            connection.setAutoCommit(true);
            tryClose(resultSet);
            tryClose(statement);
        }
    }

    private void checkUpgradeDropSpout(final Statement statement) {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SELECT * FROM `" + tablePrefix + "huds` LIMIT 1");

            ResultSetMetaData rsmeta = resultSet.getMetaData();
            boolean column_exists = false;

            for (int i = 1; i <= rsmeta.getColumnCount(); i++) {
                if (rsmeta.getColumnName(i).equalsIgnoreCase("hudtype")) {
                    column_exists = true;
                    break;
                }
            }

            if (column_exists) {
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

    private int getUserID(final Connection connection, final String playerName, final UUID uuid) {
        if (uuid == null) {
            return getUserIDByName(connection, playerName);
        }

        if (cachedUserIDs.containsKey(uuid)) {
            return cachedUserIDs.get(uuid);
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT id, `user` FROM " + tablePrefix
                    + "users WHERE uuid = ? OR (uuid IS NULL AND `user` = ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                cachedUserIDs.put(uuid, id);

                return id;
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
            tryClose(statement);
        }

        return -1;
    }

    private int getUserIDByName(final Connection connection, final String playerName) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT id, `user` FROM " + tablePrefix + "users WHERE `user` = ?");
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(resultSet);
            tryClose(statement);
        }

        return -1;
    }

    private void tryClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
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

    public enum PoolIdentifier {
        MISC,
        LOAD,
        SAVE
    }

    public void resetMobHealthSettings() {
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement(
                    "UPDATE " + tablePrefix + "huds SET mobhealthbar = ?");
            statement.setString(1, mcMMO.p.getGeneralConfig().getMobHealthbarDefault().toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            logSQLException(ex);
        } finally {
            tryClose(statement);
            tryClose(connection);
        }
    }

    private void updateCharacterSet(@NotNull Statement statement) {
        //TODO: Could check the tables for being latin1 before executing queries but it seems moot because it is likely the same computational effort
        /*
            The following columns were set to use latin1 historically (now utf8mb4)
            column user in <tablePrefix>users
            column uuid in <tablePrefix>users

            column mobhealthbar in <tablePrefix>huds
         */

        //Alter users table
        logger.info("SQL Converting tables from latin1 to utf8mb4");

        //Update "user" column
        try {
            logger.info("Updating user column to new encoding");
            statement.executeUpdate(getUpdateUserInUsersTableSQLQuery());

            //Update "uuid" column
            logger.info("Updating user column to new encoding");
            statement.executeUpdate(getUpdateUUIDInUsersTableSQLQuery());

            //Update "mobhealthbar" column
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
}
