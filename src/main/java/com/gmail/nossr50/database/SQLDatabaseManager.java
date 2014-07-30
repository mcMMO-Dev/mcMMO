package com.gmail.nossr50.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.database.UUIDUpdateAsyncTask;
import com.gmail.nossr50.util.Misc;

import snaq.db.ConnectionPool;

public final class SQLDatabaseManager implements DatabaseManager {
    private static final String ALL_QUERY_VERSION = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing+alchemy";
    private String tablePrefix = Config.getInstance().getMySQLTablePrefix();

    private final int POOL_FETCH_TIMEOUT = 0; // How long a method will wait for a connection.  Since none are on main thread, we can safely say wait for as long as you like.

    private final Map<UUID, Integer> cachedUserIDs = new HashMap<UUID, Integer>();
    private final Map<String, Integer> cachedUserIDsByName = new HashMap<String, Integer>();

    private ConnectionPool connectionPool;

    protected SQLDatabaseManager() {
        try {
            // Force driver to load if not yet loaded
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", Config.getInstance().getMySQLUserName());
            connectionProperties.put("password", Config.getInstance().getMySQLUserPassword());
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("cachePrepStmts", "true");
            connectionProperties.put("prepStmtCacheSize", "64");
            connectionProperties.put("prepStmtCacheSqlLimit", "2048");
            connectionProperties.put("useServerPrepStmts", "true");
            connectionPool = new ConnectionPool("mcMMO-Pool",
                    1 /*Minimum of one*/,
                    Config.getInstance().getMySQLMaxPoolSize() /*max pool size */,
                    Config.getInstance().getMySQLMaxConnections() /*max num connections*/,
                    0 /* idle timeout of connections */,
                    "jdbc:mysql://" + Config.getInstance().getMySQLServerName() + ":" + Config.getInstance().getMySQLServerPort() + "/" + Config.getInstance().getMySQLDatabaseName(),
                    connectionProperties);
            connectionPool.init(); // Init first connection
            connectionPool.registerShutdownHook(); // Auto release on jvm exit  just in case
        }
        catch (ClassNotFoundException e) {
            // TODO tft do something here  everything will blow up
            e.printStackTrace();
        }

        checkStructure();

    }

    public void purgePowerlessUsers() {
        mcMMO.p.getLogger().info("Purging powerless users...");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> usernames = new ArrayList<String>();

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT u.user FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");

            while (resultSet.next()) {
                usernames.add(resultSet.getString("user"));
            }

            resultSet.close();

            statement.executeUpdate("DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                    "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                    "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                    "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                    "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                    "WHERE (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        if (!usernames.isEmpty()) {
            processPurge(usernames);
        }

        mcMMO.p.getLogger().info("Purged " + usernames.size() + " users from the database.");
    }

    public void purgeOldUsers() {
        mcMMO.p.getLogger().info("Purging old users...");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> usernames = new ArrayList<String>();

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT user FROM " + tablePrefix + "users WHERE ((NOW() - lastlogin * " + Misc.TIME_CONVERSION_FACTOR + ") > " + PURGE_TIME + ")");

            while (resultSet.next()) {
                usernames.add(resultSet.getString("user"));
            }

            resultSet.close();

            statement.executeUpdate("DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                    "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                    "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                    "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                    "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                    "WHERE ((NOW() - lastlogin * " + Misc.TIME_CONVERSION_FACTOR + ") > " + PURGE_TIME + ")");
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        if (!usernames.isEmpty()) {
            processPurge(usernames);
        }

        mcMMO.p.getLogger().info("Purged " + usernames.size() + " users from the database.");
    }

    public boolean removeUser(String playerName) {
        boolean success = false;
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.prepareStatement("DELETE FROM u, e, h, s, c " +
                    "USING " + tablePrefix + "users u " +
                    "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                    "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                    "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                    "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                    "WHERE u.user = ?");

            statement.setString(1, playerName);

            success = statement.executeUpdate() != 0;
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        if (success) {
            Misc.profileCleanup(playerName);
        }

        return success;
    }

    public boolean saveUser(PlayerProfile profile) {
        boolean success = true;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);

            int id = getUserID(connection, profile.getUniqueId());

            if (id == -1) {
                newUser(profile.getPlayerName(), profile.getUniqueId().toString());
                id = getUserID(connection, profile.getUniqueId());
                if (id == -1) {
                    return false;
                }
            }

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "users SET lastlogin = UNIX_TIMESTAMP() WHERE uuid = ?");
            statement.setString(1, profile.getUniqueId().toString());
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "skills SET "
                    + " taming = ?, mining = ?, repair = ?, woodcutting = ?"
                    + ", unarmed = ?, herbalism = ?, excavation = ?"
                    + ", archery = ?, swords = ?, axes = ?, acrobatics = ?"
                    + ", fishing = ?, alchemy = ? WHERE user_id = ?");
            statement.setInt(1, profile.getSkillLevel(SkillType.TAMING));
            statement.setInt(2, profile.getSkillLevel(SkillType.MINING));
            statement.setInt(3, profile.getSkillLevel(SkillType.REPAIR));
            statement.setInt(4, profile.getSkillLevel(SkillType.WOODCUTTING));
            statement.setInt(5, profile.getSkillLevel(SkillType.UNARMED));
            statement.setInt(6, profile.getSkillLevel(SkillType.HERBALISM));
            statement.setInt(7, profile.getSkillLevel(SkillType.EXCAVATION));
            statement.setInt(8, profile.getSkillLevel(SkillType.ARCHERY));
            statement.setInt(9, profile.getSkillLevel(SkillType.SWORDS));
            statement.setInt(10, profile.getSkillLevel(SkillType.AXES));
            statement.setInt(11, profile.getSkillLevel(SkillType.ACROBATICS));
            statement.setInt(12, profile.getSkillLevel(SkillType.FISHING));
            statement.setInt(13, profile.getSkillLevel(SkillType.ALCHEMY));
            statement.setInt(14, id);
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "experience SET "
                    + " taming = ?, mining = ?, repair = ?, woodcutting = ?"
                    + ", unarmed = ?, herbalism = ?, excavation = ?"
                    + ", archery = ?, swords = ?, axes = ?, acrobatics = ?"
                    + ", fishing = ?, alchemy = ? WHERE user_id = ?");
            statement.setInt(1, profile.getSkillXpLevel(SkillType.TAMING));
            statement.setInt(2, profile.getSkillXpLevel(SkillType.MINING));
            statement.setInt(3, profile.getSkillXpLevel(SkillType.REPAIR));
            statement.setInt(4, profile.getSkillXpLevel(SkillType.WOODCUTTING));
            statement.setInt(5, profile.getSkillXpLevel(SkillType.UNARMED));
            statement.setInt(6, profile.getSkillXpLevel(SkillType.HERBALISM));
            statement.setInt(7, profile.getSkillXpLevel(SkillType.EXCAVATION));
            statement.setInt(8, profile.getSkillXpLevel(SkillType.ARCHERY));
            statement.setInt(9, profile.getSkillXpLevel(SkillType.SWORDS));
            statement.setInt(10, profile.getSkillXpLevel(SkillType.AXES));
            statement.setInt(11, profile.getSkillXpLevel(SkillType.ACROBATICS));
            statement.setInt(12, profile.getSkillXpLevel(SkillType.FISHING));
            statement.setInt(13, profile.getSkillXpLevel(SkillType.ALCHEMY));
            statement.setInt(14, id);
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "cooldowns SET "
                    + "  mining = ?, woodcutting = ?, unarmed = ?"
                    + ", herbalism = ?, excavation = ?, swords = ?"
                    + ", axes = ?, blast_mining = ? WHERE user_id = ?");
            statement.setLong(1, profile.getAbilityDATS(AbilityType.SUPER_BREAKER));
            statement.setLong(2, profile.getAbilityDATS(AbilityType.TREE_FELLER));
            statement.setLong(3, profile.getAbilityDATS(AbilityType.BERSERK));
            statement.setLong(4, profile.getAbilityDATS(AbilityType.GREEN_TERRA));
            statement.setLong(5, profile.getAbilityDATS(AbilityType.GIGA_DRILL_BREAKER));
            statement.setLong(6, profile.getAbilityDATS(AbilityType.SERRATED_STRIKES));
            statement.setLong(7, profile.getAbilityDATS(AbilityType.SKULL_SPLITTER));
            statement.setLong(8, profile.getAbilityDATS(AbilityType.BLAST_MINING));
            statement.setInt(9, id);
            success = (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "huds SET mobhealthbar = ? WHERE user_id = ?");
            statement.setString(1, profile.getMobHealthbarType() == null ? Config.getInstance().getMobHealthbarDefault().name() : profile.getMobHealthbarType().name());
            statement.setInt(2, id);
            success = (statement.executeUpdate() != 0);
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return success;
    }

    public List<PlayerStat> readLeaderboard(SkillType skill, int pageNumber, int statsPerPage) {
        List<PlayerStat> stats = new ArrayList<PlayerStat>();

        String query = skill == null ? ALL_QUERY_VERSION : skill.name().toLowerCase();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.prepareStatement("SELECT " + query + ", user, NOW() FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON (user_id = id) WHERE " + query + " > 0 ORDER BY " + query + " DESC, user LIMIT ?, ?");
            statement.setInt(1, (pageNumber * statsPerPage) - statsPerPage);
            statement.setInt(2, statsPerPage);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ArrayList<String> column = new ArrayList<String>();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    column.add(resultSet.getString(i));
                }

                stats.add(new PlayerStat(column.get(1), Integer.valueOf(column.get(0))));
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return stats;
    }

    public Map<SkillType, Integer> readRank(String playerName) {
        Map<SkillType, Integer> skills = new EnumMap<SkillType, Integer>(SkillType.class);

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
                String skillName = skillType.name().toLowerCase();
                String sql = "SELECT COUNT(*) AS rank FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                        "AND " + skillName + " > (SELECT " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                        "WHERE user = ?)";

                statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                resultSet = statement.executeQuery();

                resultSet.next();

                int rank = resultSet.getInt("rank");

                sql = "SELECT user, " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                        "AND " + skillName + " = (SELECT " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                        "WHERE user = '" + playerName + "') ORDER BY user";

                resultSet.close();
                statement.close();

                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getString("user").equalsIgnoreCase(playerName)) {
                        skills.put(skillType, rank + resultSet.getRow());
                        break;
                    }
                }

                resultSet.close();
                statement.close();
            }

            String sql = "SELECT COUNT(*) AS rank FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                    "WHERE " + ALL_QUERY_VERSION + " > 0 " +
                    "AND " + ALL_QUERY_VERSION + " > " +
                    "(SELECT " + ALL_QUERY_VERSION + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE user = ?)";

            statement = connection.prepareStatement(sql);
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            resultSet.next();

            int rank = resultSet.getInt("rank");

            resultSet.close();
            statement.close();

            sql = "SELECT user, " + ALL_QUERY_VERSION + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                    "WHERE " + ALL_QUERY_VERSION + " > 0 " +
                    "AND " + ALL_QUERY_VERSION + " = " +
                    "(SELECT " + ALL_QUERY_VERSION + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE user = ?) ORDER BY user";

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
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return skills;
    }

    public void newUser(String playerName, String uuid) {
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            newUser(connection, playerName, uuid);
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void newUser(Connection connection, String playerName, String uuid) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "users (user, uuid, lastlogin) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playerName);
            statement.setString(2, uuid);
            statement.setLong(3, System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();

            if (!resultSet.next()) {
                return;
            }

            writeMissingRows(connection, resultSet.getInt(1));
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * This is a fallback method to provide the old way of getting a
     * PlayerProfile in case there is no UUID match found
     */
    private PlayerProfile loadPlayerNameProfile(String playerName, String uuid, boolean create, boolean retry) {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            int id = getUserID(connection, playerName);

            if (id == -1) {
                // There is no such user
                if (create) {
                    newUser(playerName, uuid);
                    return loadPlayerNameProfile(playerName, uuid, false, false);
                }

                // Return unloaded profile if can't create
                return new PlayerProfile(playerName, false);
            }
            // There is such a user
            writeMissingRows(connection, id);

            statement = connection.prepareStatement(
                    "SELECT "
                            + "s.taming, s.mining, s.repair, s.woodcutting, s.unarmed, s.herbalism, s.excavation, s.archery, s.swords, s.axes, s.acrobatics, s.fishing, s.alchemy, "
                            + "e.taming, e.mining, e.repair, e.woodcutting, e.unarmed, e.herbalism, e.excavation, e.archery, e.swords, e.axes, e.acrobatics, e.fishing, e.alchemy, "
                            + "c.taming, c.mining, c.repair, c.woodcutting, c.unarmed, c.herbalism, c.excavation, c.archery, c.swords, c.axes, c.acrobatics, c.blast_mining, "
                            + "h.mobhealthbar, u.uuid "
                            + "FROM " + tablePrefix + "users u "
                            + "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) "
                            + "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) "
                            + "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) "
                            + "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) "
                            + "WHERE u.user = ?");
            statement.setString(1, playerName);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    PlayerProfile ret = loadFromResult(playerName, resultSet);
                    return ret;
                }
                catch (SQLException e) {
                }
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        // Problem, nothing was returned

        // Quit if this is second time around
        if (!retry) {
            return new PlayerProfile(playerName, false);
        }

        // Retry, and abort on re-failure
        return loadPlayerNameProfile(playerName, uuid, create, false);
    }

    @Deprecated
    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        return loadPlayerProfile(playerName, "", false, true);
    }

    public PlayerProfile loadPlayerProfile(UUID uuid) {
        return loadPlayerProfile("", uuid.toString(), false, true);
    }

    public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create) {
        return loadPlayerProfile(playerName, uuid.toString(), create, true);
    }

    private PlayerProfile loadPlayerProfile(String playerName, String uuid, boolean create, boolean retry) {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            int id = getUserID(connection, playerName);

            if (id == -1) {
                // There is no such user
                if (create) {
                    newUser(playerName, uuid);
                    return loadPlayerNameProfile(playerName, uuid, false, false);
                }

                // Return unloaded profile if can't create
                return new PlayerProfile(playerName, false);
            }
            // There is such a user
            writeMissingRows(connection, id);

            statement = connection.prepareStatement(
                    "SELECT "
                            + "s.taming, s.mining, s.repair, s.woodcutting, s.unarmed, s.herbalism, s.excavation, s.archery, s.swords, s.axes, s.acrobatics, s.fishing, s.alchemy, "
                            + "e.taming, e.mining, e.repair, e.woodcutting, e.unarmed, e.herbalism, e.excavation, e.archery, e.swords, e.axes, e.acrobatics, e.fishing, e.alchemy, "
                            + "c.taming, c.mining, c.repair, c.woodcutting, c.unarmed, c.herbalism, c.excavation, c.archery, c.swords, c.axes, c.acrobatics, c.blast_mining, "
                            + "h.mobhealthbar, u.uuid "
                            + "FROM " + tablePrefix + "users u "
                            + "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) "
                            + "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) "
                            + "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) "
                            + "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) "
                            + "WHERE u.UUID = ?");
            statement.setString(1, uuid);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    PlayerProfile profile = loadFromResult(playerName, resultSet);
                    resultSet.close();
                    statement.close();

                    if (!playerName.isEmpty() && !profile.getPlayerName().isEmpty()) {
                        statement = connection.prepareStatement(
                                "UPDATE `" + tablePrefix + "users` "
                                        + "SET user = ? "
                                        + "WHERE UUID = ?");
                        statement.setString(1, playerName);
                        statement.setString(2, uuid);
                        statement.executeUpdate();
                        statement.close();
                    }

                    return profile;
                }
                catch (SQLException e) {
                }
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        // Problem, nothing was returned

        // Retry the old fashioned way if this is second time around
        if (!retry) {
            return loadPlayerNameProfile(playerName, uuid, create, true);
        }

        // Retry, and abort on re-failure
        return loadPlayerProfile(playerName, uuid, create, false);
    }

    public void convertUsers(DatabaseManager destination) {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.prepareStatement(
                    "SELECT "
                            + "s.taming, s.mining, s.repair, s.woodcutting, s.unarmed, s.herbalism, s.excavation, s.archery, s.swords, s.axes, s.acrobatics, s.fishing, s.alchemy, "
                            + "e.taming, e.mining, e.repair, e.woodcutting, e.unarmed, e.herbalism, e.excavation, e.archery, e.swords, e.axes, e.acrobatics, e.fishing, e.alchemy, "
                            + "c.taming, c.mining, c.repair, c.woodcutting, c.unarmed, c.herbalism, c.excavation, c.archery, c.swords, c.axes, c.acrobatics, c.blast_mining, "
                            + "h.mobhealthbar, u.uuid "
                            + "FROM " + tablePrefix + "users u "
                            + "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) "
                            + "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) "
                            + "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) "
                            + "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) "
                            + "WHERE u.user = ?");
            List<String> usernames = getStoredUsers();
            int convertedUsers = 0;
            long startMillis = System.currentTimeMillis();
            for (String playerName : usernames) {
                statement.setString(1, playerName);
                try {
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    destination.saveUser(loadFromResult(playerName, resultSet));
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
                convertedUsers++;
                Misc.printProgress(convertedUsers, progressInterval, startMillis);
            }
        }
        catch (SQLException e) {
            printErrors(e);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

    }

    public boolean saveUserUUID(String userName, UUID uuid) {
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.prepareStatement(
                    "UPDATE `" + tablePrefix + "users` SET "
                            + "  uuid = ? WHERE user = ?");
            statement.setString(1, uuid.toString());
            statement.setString(2, userName);
            statement.execute();
            return true;
        }
        catch (SQLException ex) {
            printErrors(ex);
            return false;
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    public boolean saveUserUUIDs(Map<String, UUID> fetchedUUIDs) {
        PreparedStatement statement = null;
        int count = 0;

        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.prepareStatement("UPDATE " + tablePrefix + "users SET uuid = ? WHERE user = ?");

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
        }
        catch (SQLException ex) {
            printErrors(ex);
            return false;
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    public List<String> getStoredUsers() {
        ArrayList<String> users = new ArrayList<String>();

        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT user FROM " + tablePrefix + "users");
            while (resultSet.next()) {
                users.add(resultSet.getString("user"));
            }
        }
        catch (SQLException e) {
            printErrors(e);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return users;
    }

    /**
     * Checks that the database structure is present and correct
     */
    private void checkStructure() {

        Statement statement = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection(POOL_FETCH_TIMEOUT);
            statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` ("
                    + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                    + "`user` varchar(40) NOT NULL,"
                    + "`uuid` varchar(36) NULL DEFAULT NULL,"
                    + "`lastlogin` int(32) unsigned NOT NULL,"
                    + "PRIMARY KEY (`id`),"
                    + "UNIQUE KEY `user` (`user`),"
                    + "UNIQUE KEY `uuid` (`uuid`)) DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "huds` ("
                    + "`user_id` int(10) unsigned NOT NULL,"
                    + "`mobhealthbar` varchar(50) NOT NULL DEFAULT '" + Config.getInstance().getMobHealthbarDefault() + "',"
                    + "PRIMARY KEY (`user_id`)) "
                    + "DEFAULT CHARSET=latin1;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "cooldowns` ("
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
                    + "PRIMARY KEY (`user_id`)) "
                    + "DEFAULT CHARSET=latin1;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "skills` ("
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
                    + "PRIMARY KEY (`user_id`)) "
                    + "DEFAULT CHARSET=latin1;");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "experience` ("
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
                    + "PRIMARY KEY (`user_id`)) "
                    + "DEFAULT CHARSET=latin1;");

            for (UpgradeType updateType : UpgradeType.values()) {
                checkDatabaseStructure(connection, updateType);
            }

            mcMMO.p.getLogger().info("Killing orphans");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "experience` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "experience`.`user_id` = `u`.`id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "huds` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "huds`.`user_id` = `u`.`id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "cooldowns` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "cooldowns`.`user_id` = `u`.`id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "skills` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "skills`.`user_id` = `u`.`id`)");
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

    }

    /**
     * Check database structure for necessary upgrades.
     *
     * @param upgrade Upgrade to attempt to apply
     */
    private void checkDatabaseStructure(Connection connection, UpgradeType upgrade) {
        if (!mcMMO.getUpgradeManager().shouldUpgrade(upgrade)) {
            mcMMO.p.debug("Skipping " + upgrade.name() + " upgrade (unneeded)");
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
                    checkUpgradeAddSQLIndexes(statement);
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

                default:
                    break;

            }

            mcMMO.getUpgradeManager().setUpgradeCompleted(upgrade);
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void writeMissingRows(Connection connection, int id) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "experience (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "skills (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "cooldowns (user_id) VALUES (?)");
            statement.setInt(1, id);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("INSERT IGNORE INTO " + tablePrefix + "huds (user_id, mobhealthbar) VALUES (?, ?)");
            statement.setInt(1, id);
            statement.setString(2, Config.getInstance().getMobHealthbarDefault().name());
            statement.execute();
            statement.close();
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void processPurge(Collection<String> usernames) {
        for (String user : usernames) {
            Misc.profileCleanup(user);
        }
    }

    private PlayerProfile loadFromResult(String playerName, ResultSet result) throws SQLException {
        Map<SkillType, Integer> skills = new EnumMap<SkillType, Integer>(SkillType.class); // Skill & Level
        Map<SkillType, Float> skillsXp = new EnumMap<SkillType, Float>(SkillType.class); // Skill & XP
        Map<AbilityType, Integer> skillsDATS = new EnumMap<AbilityType, Integer>(AbilityType.class); // Ability & Cooldown
        MobHealthbarType mobHealthbarType;
        UUID uuid;

        final int OFFSET_SKILLS = 0; // TODO update these numbers when the query
        // changes (a new skill is added)
        final int OFFSET_XP = 13;
        final int OFFSET_DATS = 26;
        final int OFFSET_OTHER = 38;

        skills.put(SkillType.TAMING, result.getInt(OFFSET_SKILLS + 1));
        skills.put(SkillType.MINING, result.getInt(OFFSET_SKILLS + 2));
        skills.put(SkillType.REPAIR, result.getInt(OFFSET_SKILLS + 3));
        skills.put(SkillType.WOODCUTTING, result.getInt(OFFSET_SKILLS + 4));
        skills.put(SkillType.UNARMED, result.getInt(OFFSET_SKILLS + 5));
        skills.put(SkillType.HERBALISM, result.getInt(OFFSET_SKILLS + 6));
        skills.put(SkillType.EXCAVATION, result.getInt(OFFSET_SKILLS + 7));
        skills.put(SkillType.ARCHERY, result.getInt(OFFSET_SKILLS + 8));
        skills.put(SkillType.SWORDS, result.getInt(OFFSET_SKILLS + 9));
        skills.put(SkillType.AXES, result.getInt(OFFSET_SKILLS + 10));
        skills.put(SkillType.ACROBATICS, result.getInt(OFFSET_SKILLS + 11));
        skills.put(SkillType.FISHING, result.getInt(OFFSET_SKILLS + 12));
        skills.put(SkillType.ALCHEMY, result.getInt(OFFSET_SKILLS + 13));

        skillsXp.put(SkillType.TAMING, result.getFloat(OFFSET_XP + 1));
        skillsXp.put(SkillType.MINING, result.getFloat(OFFSET_XP + 2));
        skillsXp.put(SkillType.REPAIR, result.getFloat(OFFSET_XP + 3));
        skillsXp.put(SkillType.WOODCUTTING, result.getFloat(OFFSET_XP + 4));
        skillsXp.put(SkillType.UNARMED, result.getFloat(OFFSET_XP + 5));
        skillsXp.put(SkillType.HERBALISM, result.getFloat(OFFSET_XP + 6));
        skillsXp.put(SkillType.EXCAVATION, result.getFloat(OFFSET_XP + 7));
        skillsXp.put(SkillType.ARCHERY, result.getFloat(OFFSET_XP + 8));
        skillsXp.put(SkillType.SWORDS, result.getFloat(OFFSET_XP + 9));
        skillsXp.put(SkillType.AXES, result.getFloat(OFFSET_XP + 10));
        skillsXp.put(SkillType.ACROBATICS, result.getFloat(OFFSET_XP + 11));
        skillsXp.put(SkillType.FISHING, result.getFloat(OFFSET_XP + 12));
        skillsXp.put(SkillType.ALCHEMY, result.getFloat(OFFSET_XP + 13));

        // Taming - Unused - result.getInt(OFFSET_DATS + 1)
        skillsDATS.put(AbilityType.SUPER_BREAKER, result.getInt(OFFSET_DATS + 2));
        // Repair - Unused - result.getInt(OFFSET_DATS + 3)
        skillsDATS.put(AbilityType.TREE_FELLER, result.getInt(OFFSET_DATS + 4));
        skillsDATS.put(AbilityType.BERSERK, result.getInt(OFFSET_DATS + 5));
        skillsDATS.put(AbilityType.GREEN_TERRA, result.getInt(OFFSET_DATS + 6));
        skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, result.getInt(OFFSET_DATS + 7));
        // Archery - Unused - result.getInt(OFFSET_DATS + 8)
        skillsDATS.put(AbilityType.SERRATED_STRIKES, result.getInt(OFFSET_DATS + 9));
        skillsDATS.put(AbilityType.SKULL_SPLITTER, result.getInt(OFFSET_DATS + 10));
        // Acrobatics - Unused - result.getInt(OFFSET_DATS + 11)
        skillsDATS.put(AbilityType.BLAST_MINING, result.getInt(OFFSET_DATS + 12));

        try {
            mobHealthbarType = MobHealthbarType.valueOf(result.getString(OFFSET_OTHER + 2));
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        try {
            uuid = UUID.fromString(result.getString(OFFSET_OTHER + 3));
        }
        catch (Exception e) {
            uuid = null;
        }

        return new PlayerProfile(playerName, uuid, skills, skillsXp, skillsDATS, mobHealthbarType);
    }

    private void printErrors(SQLException ex) {
        mcMMO.p.getLogger().severe("SQLException: " + ex.getMessage());
        mcMMO.p.getLogger().severe("SQLState: " + ex.getSQLState());
        mcMMO.p.getLogger().severe("VendorError: " + ex.getErrorCode());
    }

    public DatabaseType getDatabaseType() {
        return DatabaseType.SQL;
    }

    private void checkUpgradeAddAlchemy(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `alchemy` FROM `" + tablePrefix + "skills` LIMIT 1");
        }
        catch (SQLException ex) {
            mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Alchemy...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "skills` ADD `alchemy` int(10) NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "experience` ADD `alchemy` int(10) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddBlastMiningCooldown(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `blast_mining` FROM `" + tablePrefix + "cooldowns` LIMIT 1");
        }
        catch (SQLException ex) {
            mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Blast Mining...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` ADD `blast_mining` int(32) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddFishing(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `fishing` FROM `" + tablePrefix + "skills` LIMIT 1");
        }
        catch (SQLException ex) {
            mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Fishing...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "skills` ADD `fishing` int(10) NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "experience` ADD `fishing` int(10) NOT NULL DEFAULT '0'");
        }
    }

    private void checkUpgradeAddMobHealthbars(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `mobhealthbar` FROM `" + tablePrefix + "huds` LIMIT 1");
        }
        catch (SQLException ex) {
            mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for mob healthbars...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "huds` ADD `mobhealthbar` varchar(50) NOT NULL DEFAULT '" + Config.getInstance().getMobHealthbarDefault() + "'");
        }
    }

    private void checkUpgradeAddSQLIndexes(final Statement statement) throws SQLException {
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery("SHOW INDEX FROM `" + tablePrefix + "skills` WHERE `Key_name` LIKE 'idx\\_%'");
            resultSet.last();

            if (resultSet.getRow() != SkillType.NON_CHILD_SKILLS.size()) {
                mcMMO.p.getLogger().info("Indexing tables, this may take a while on larger databases");

                for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
                    String skill_name = skill.name().toLowerCase();

                    try {
                        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "skills` ADD INDEX `idx_" + skill_name + "` (`" + skill_name + "`) USING BTREE");
                    }
                    catch (SQLException ex) {
                        // Ignore
                    }
                }
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private void checkUpgradeAddUUIDs(final Statement statement) {
        List<String> names = new ArrayList<String>();
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
                mcMMO.p.getLogger().info("Adding UUIDs to mcMMO MySQL user table...");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix + "users` ADD `uuid` varchar(36) NULL DEFAULT NULL");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix + "users` ADD UNIQUE INDEX `uuid` (`uuid`) USING BTREE");
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        try {
            resultSet = statement.executeQuery("SELECT `user` FROM `" + tablePrefix + "users` WHERE `uuid` IS NULL");

            while (resultSet.next()) {
                names.add(resultSet.getString("user"));
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        if (!names.isEmpty()) {
            new UUIDUpdateAsyncTask(mcMMO.p, names).runTaskAsynchronously(mcMMO.p);
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
                mcMMO.p.getLogger().info("Removing party name from users table...");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix + "users` DROP COLUMN `party`");
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
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
                mcMMO.p.getLogger().info("Removing Spout HUD type from huds table...");
                statement.executeUpdate("ALTER TABLE `" + tablePrefix + "huds` DROP COLUMN `hudtype`");
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private int getUserID(final Connection connection, final String playerName) {
        Integer id = cachedUserIDsByName.get(playerName.toLowerCase());
        if (id != null) {
            return id;
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT id, uuid FROM " + tablePrefix + "users WHERE user = ?");
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("id");

                cachedUserIDsByName.put(playerName.toLowerCase(), id);

                try {
                    cachedUserIDs.put(UUID.fromString(resultSet.getString("uuid")), id);
                }
                catch (Exception e) {

                }

                return id;
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return -1;
    }

    private int getUserID(final Connection connection, final UUID uuid) {
        if (cachedUserIDs.containsKey(uuid)) {
            return cachedUserIDs.get(uuid);
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT id, user FROM " + tablePrefix + "users WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                cachedUserIDs.put(uuid, id);
                cachedUserIDsByName.put(resultSet.getString("user").toLowerCase(), id);

                return id;
            }
        }
        catch (SQLException ex) {
            printErrors(ex);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore
                }
            }
        }

        return -1;
    }

    @Override
    public void onDisable() {
        connectionPool.release();
    }
}
