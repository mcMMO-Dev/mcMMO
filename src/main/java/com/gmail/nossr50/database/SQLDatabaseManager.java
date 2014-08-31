package com.gmail.nossr50.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.scheduler.BukkitRunnable;

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
    private String tablePrefix = Config.getInstance().getMySQLTablePrefix();

    private final int POOL_FETCH_TIMEOUT = 360000;

    private final Map<UUID, Integer> cachedUserIDs = new HashMap<UUID, Integer>();

    private ConnectionPool miscPool;
    private ConnectionPool loadPool;
    private ConnectionPool savePool;

    private ReentrantLock massUpdateLock = new ReentrantLock();

    protected SQLDatabaseManager() {
        String connectionString = "jdbc:mysql://" + Config.getInstance().getMySQLServerName() + ":" + Config.getInstance().getMySQLServerPort() + "/" + Config.getInstance().getMySQLDatabaseName();

        try {
            // Force driver to load if not yet loaded
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
            //throw e; // aborts onEnable()  Riking if you want to do this, fully implement it.
        }

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", Config.getInstance().getMySQLUserName());
        connectionProperties.put("password", Config.getInstance().getMySQLUserPassword());
        connectionProperties.put("autoReconnect", "true");
        connectionProperties.put("cachePrepStmts", "true");
        connectionProperties.put("prepStmtCacheSize", "64");
        connectionProperties.put("prepStmtCacheSqlLimit", "2048");
        connectionProperties.put("useServerPrepStmts", "true");
        miscPool = new ConnectionPool("mcMMO-Misc-Pool",
                0 /*No Minimum really needed*/,
                Config.getInstance().getMySQLMaxPoolSize(PoolIdentifier.MISC) /*max pool size */,
                Config.getInstance().getMySQLMaxConnections(PoolIdentifier.MISC) /*max num connections*/,
                400 /* idle timeout of connections */,
                connectionString,
                connectionProperties);
        loadPool = new ConnectionPool("mcMMO-Load-Pool",
                1 /*Minimum of one*/,
                Config.getInstance().getMySQLMaxPoolSize(PoolIdentifier.LOAD) /*max pool size */,
                Config.getInstance().getMySQLMaxConnections(PoolIdentifier.LOAD) /*max num connections*/,
                400 /* idle timeout of connections */,
                connectionString,
                connectionProperties);
        savePool = new ConnectionPool("mcMMO-Save-Pool",
                1 /*Minimum of one*/,
                Config.getInstance().getMySQLMaxPoolSize(PoolIdentifier.SAVE) /*max pool size */,
                Config.getInstance().getMySQLMaxConnections(PoolIdentifier.SAVE) /*max num connections*/,
                400 /* idle timeout of connections */,
                connectionString,
                connectionProperties);
        miscPool.init(); // Init first connection
        miscPool.registerShutdownHook(); // Auto release on jvm exit  just in case
        loadPool.init();
        loadPool.registerShutdownHook();
        savePool.init();
        savePool.registerShutdownHook();

        checkStructure();

    }

    public void purgePowerlessUsers() {
        massUpdateLock.lock();
        mcMMO.p.getLogger().info("Purging powerless users...");

        Connection connection = null;
        Statement statement = null;
        int purged = 0;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.createStatement();

            String deleteFrom = "DELETE FROM " + tablePrefix + "skills WHERE ";
            deleteFrom += com.gmail.nossr50.util.StringUtils.createStringFromListWithNoPrefixBeforeFirst(SkillType.getLowerSkillNames(), " AND ", " = 0");
            deleteFrom += ";";
            purged = statement.executeUpdate(deleteFrom);

            statement.executeUpdate("DELETE FROM `" + tablePrefix + "experience` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "skills` `s` WHERE `" + tablePrefix + "experience`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "huds` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "skills` `s` WHERE `" + tablePrefix + "huds`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "cooldowns` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "skills` `s` WHERE `" + tablePrefix + "cooldowns`.`user_id` = `s`.`user_id`)");
            statement.executeUpdate("DELETE FROM `" + tablePrefix + "users` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "skills` `s` WHERE `" + tablePrefix + "users`.`id` = `s`.`user_id`)");
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
            massUpdateLock.unlock();
        }

        mcMMO.p.getLogger().info("Purged " + purged + " users from the database.");
    }

    public void purgeOldUsers() {
        massUpdateLock.lock();
        mcMMO.p.getLogger().info("Purging inactive users older than " + (PURGE_TIME / 2630000L) + " months...");

        Connection connection = null;
        Statement statement = null;
        int purged = 0;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.createStatement();

            purged = statement.executeUpdate("DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                    "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                    "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                    "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                    "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                    "WHERE ((UNIX_TIMESTAMP() - lastlogin) > " + PURGE_TIME + ")");
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
            massUpdateLock.unlock();
        }

        mcMMO.p.getLogger().info("Purged " + purged + " users from the database.");
    }

    public boolean removeUser(String playerName) {
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
            connection = getConnection(PoolIdentifier.SAVE);

            int id = getUserID(connection, profile.getPlayerName(), profile.getUniqueId());

            if (id == -1) {
                id = newUser(connection, profile.getPlayerName(), profile.getUniqueId());
                if (id == -1) {
                    return false;
                }
            }

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "users SET lastlogin = UNIX_TIMESTAMP() WHERE id = ?");
            statement.setInt(1, id);
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "skills SET   " 
            		+ com.gmail.nossr50.util.StringUtils.createStringFromListWithNoPrefixBeforeFirst(SkillType.getLowerSkillNames(), ", ", " = ?")
                    + " WHERE user_id = ?");
            for(int i = 0; i < SkillType.getNonChildSkills().size(); i++) {
            	statement.setInt(i + 1, profile.getSkillLevel(SkillType.getNonChildSkills().get(i)));
            }
            statement.setInt(SkillType.getNonChildSkills().size() + 1, id);
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "experience SET   " 
            		+ com.gmail.nossr50.util.StringUtils.createStringFromListWithNoPrefixBeforeFirst(SkillType.getLowerSkillNames(), ", ", " = ?")
                    + " WHERE user_id = ?");
            for(int i = 0; i < SkillType.getNonChildSkills().size(); i++) {
            	statement.setInt(i + 1, profile.getSkillXpLevel(SkillType.getNonChildSkills().get(i)));
            }
            statement.setInt(SkillType.getNonChildSkills().size() + 1, id);
            success &= (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "cooldowns SET   "
            		+ com.gmail.nossr50.util.StringUtils.createStringFromListWithNoPrefixBeforeFirst(AbilityType.getLowerAbilitieNames(), ", ", " = ?")
                    + " WHERE user_id = ?");
            for(int i = 0; i < AbilityType.getAbilities().size(); i++) {
            	statement.setLong(i + 1, profile.getAbilityDATS(AbilityType.getAbilities().get(i)));
            }
            statement.setInt(AbilityType.getAbilities().size() + 1, id);
            success = (statement.executeUpdate() != 0);
            statement.close();

            statement = connection.prepareStatement("UPDATE " + tablePrefix + "huds SET mobhealthbar = ? WHERE user_id = ?");
            statement.setString(1, profile.getMobHealthbarType() == null ? Config.getInstance().getMobHealthbarDefault().name() : profile.getMobHealthbarType().name());
            statement.setInt(2, id);
            success = (statement.executeUpdate() != 0);
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

        String query = skill == null ? getAllQueryVersion() : skill.getName().toLowerCase();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
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
        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>();

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            for (SkillType skillType : SkillType.getNonChildSkills()) {
                String skillName = skillType.getName().toLowerCase();
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
                    "WHERE " + getAllQueryVersion() + " > 0 " +
                    "AND " + getAllQueryVersion() + " > " +
                    "(SELECT " + getAllQueryVersion() + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE user = ?)";

            statement = connection.prepareStatement(sql);
            statement.setString(1, playerName);
            resultSet = statement.executeQuery();

            resultSet.next();

            int rank = resultSet.getInt("rank");

            resultSet.close();
            statement.close();

            sql = "SELECT user, " + getAllQueryVersion() + " " +
                    "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                    "WHERE " + getAllQueryVersion() + " > 0 " +
                    "AND " + getAllQueryVersion() + " = " +
                    "(SELECT " + getAllQueryVersion() + " " +
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

    public void newUser(String playerName, UUID uuid) {
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
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

    private int newUser(Connection connection, String playerName, UUID uuid) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "users (user, uuid, lastlogin) VALUES (?, ?, UNIX_TIMESTAMP())", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playerName);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();

            if (!resultSet.next()) {
                return -1;
            }

            writeMissingRows(connection, resultSet.getInt(1));
            return resultSet.getInt(1);
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

    @Deprecated
    public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
        return loadPlayerProfile(playerName, null, false, true);
    }

    public PlayerProfile loadPlayerProfile(UUID uuid) {
        return loadPlayerProfile("", uuid, false, true);
    }

    public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create) {
        return loadPlayerProfile(playerName, uuid, create, true);
    }

    private PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create, boolean retry) {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(PoolIdentifier.LOAD);
            int id = getUserID(connection, playerName, uuid);

            if (id == -1) {
                // There is no such user
                if (create) {
                    id = newUser(connection, playerName, uuid);
                    create = false;
                    if (id == -1) {
                        return new PlayerProfile(playerName, false);
                    }
                } else {
                    return new PlayerProfile(playerName, false);
                }
            }
            // There is such a user
            writeMissingRows(connection, id);

            addNewSkills(connection);
            
            statement = connection.prepareStatement(
                    "SELECT "
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "s.", ", ")
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "e.", ", ")
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(AbilityType.getLowerAbilitieNames(), "c.", ", ")
                            + "h.mobhealthbar, u.uuid "
                            + "FROM " + tablePrefix + "users u "
                            + "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) "
                            + "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) "
                            + "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) "
                            + "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) "
                            + "WHERE u.id = ?");
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    PlayerProfile profile = loadFromResult(playerName, resultSet);
                    resultSet.close();
                    statement.close();

                    if (!playerName.isEmpty() && !profile.getPlayerName().isEmpty()) {
                        statement = connection.prepareStatement(
                                "UPDATE `" + tablePrefix + "users` "
                                        + "SET user = ?, uuid = ? "
                                        + "WHERE id = ?");
                        statement.setString(1, playerName);
                        statement.setString(2, uuid.toString());
                        statement.setInt(3, id);
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

        // return unloaded profile
        if (!retry) {
            return new PlayerProfile(playerName, false);
        }

        // Retry, and abort on re-failure
        return loadPlayerProfile(playerName, uuid, create, false);
    }

    public void convertUsers(DatabaseManager destination) {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement(
                    "SELECT "
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "s.", ", ")
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "e.", ", ")
            				+ com.gmail.nossr50.util.StringUtils.createStringFromList(AbilityType.getLowerAbilitieNames(), "c.", ", ")
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
            connection = getConnection(PoolIdentifier.MISC);
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
            connection = getConnection(PoolIdentifier.MISC);
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
            connection = getConnection(PoolIdentifier.MISC);
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

        PreparedStatement statement = null;
        Statement createStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = getConnection(PoolIdentifier.MISC);
            statement = connection.prepareStatement("SELECT table_name FROM INFORMATION_SCHEMA.TABLES"
                    + " WHERE table_schema = ?"
                    + " AND table_name = ?");
            statement.setString(1, Config.getInstance().getMySQLDatabaseName());
            statement.setString(2, tablePrefix + "users");
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                createStatement = connection.createStatement();
                createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` ("
                    + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                    + "`user` varchar(40) NOT NULL,"
                    + "`uuid` varchar(36) NULL DEFAULT NULL,"
                    + "`lastlogin` int(32) unsigned NOT NULL,"
                    + "PRIMARY KEY (`id`),"
                    + "UNIQUE KEY `user` (`user`),"
                    + "UNIQUE KEY `uuid` (`uuid`)) DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
                createStatement.close();
            }
            resultSet.close();
            statement.setString(1, Config.getInstance().getMySQLDatabaseName());
            statement.setString(2, tablePrefix + "huds");
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                createStatement = connection.createStatement();
                createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "huds` ("
                        + "`user_id` int(10) unsigned NOT NULL,"
                        + "`mobhealthbar` varchar(50) NOT NULL DEFAULT '" + Config.getInstance().getMobHealthbarDefault() + "',"
                        + "PRIMARY KEY (`user_id`)) "
                        + "DEFAULT CHARSET=latin1;");
                createStatement.close();
            }
            resultSet.close();
            statement.setString(1, Config.getInstance().getMySQLDatabaseName());
            statement.setString(2, tablePrefix + "cooldowns");
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                createStatement = connection.createStatement();
                createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "cooldowns` ("
                        + "`user_id` int(10) unsigned NOT NULL,"
                		+ com.gmail.nossr50.util.StringUtils.createStringFromList(AbilityType.getLowerAbilitieNames(), "`", "` int(32) unsigned NOT NULL DEFAULT '0',")
                        + "PRIMARY KEY (`user_id`)) "
                        + "DEFAULT CHARSET=latin1;");
                createStatement.close();
            }
            resultSet.close();
            statement.setString(1, Config.getInstance().getMySQLDatabaseName());
            statement.setString(2, tablePrefix + "skills");
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                createStatement = connection.createStatement();
                createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "skills` ("
                        + "`user_id` int(10) unsigned NOT NULL,"
                		+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "`", "` int(10) unsigned NOT NULL DEFAULT '0',")
                        + "PRIMARY KEY (`user_id`)) "
                        + "DEFAULT CHARSET=latin1;");
                createStatement.close();
            }
            resultSet.close();
            statement.setString(1, Config.getInstance().getMySQLDatabaseName());
            statement.setString(2, tablePrefix + "experience");
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                createStatement = connection.createStatement();
                createStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "experience` ("
                        + "`user_id` int(10) unsigned NOT NULL,"
                		+ com.gmail.nossr50.util.StringUtils.createStringFromList(SkillType.getLowerSkillNames(), "`", "` int(10) unsigned NOT NULL DEFAULT '0',")
                        + "PRIMARY KEY (`user_id`)) "
                        + "DEFAULT CHARSET=latin1;");
                createStatement.close();
            }
            resultSet.close();
            statement.close();

            for (UpgradeType updateType : UpgradeType.values()) {
                checkDatabaseStructure(connection, updateType);
            }

            mcMMO.p.getLogger().info("Killing orphans");
            createStatement = connection.createStatement();
            createStatement.executeUpdate("DELETE FROM `" + tablePrefix + "experience` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "experience`.`user_id` = `u`.`id`)");
            createStatement.executeUpdate("DELETE FROM `" + tablePrefix + "huds` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "huds`.`user_id` = `u`.`id`)");
            createStatement.executeUpdate("DELETE FROM `" + tablePrefix + "cooldowns` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "cooldowns`.`user_id` = `u`.`id`)");
            createStatement.executeUpdate("DELETE FROM `" + tablePrefix + "skills` WHERE NOT EXISTS (SELECT * FROM `" + tablePrefix + "users` `u` WHERE `" + tablePrefix + "skills`.`user_id` = `u`.`id`)");
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
            if (createStatement != null) {
                try {
                    createStatement.close();
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

    private Connection getConnection(PoolIdentifier identifier) throws SQLException {
        Connection connection = null;
        switch (identifier) {
            case LOAD:
                connection = loadPool.getConnection(POOL_FETCH_TIMEOUT);
                break;
            case MISC:
                connection = miscPool.getConnection(POOL_FETCH_TIMEOUT);
                break;
            case SAVE:
                connection = savePool.getConnection(POOL_FETCH_TIMEOUT);
                break;
        }
        if (connection == null) {
            throw new RuntimeException("getConnection() for " + identifier.name().toLowerCase() + " pool timed out.  Increase max connections settings.");
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
                    
                case CHANGE_SQL_COOLDOWN_NAMES:
                	checkUpgradeChangeCooldownNames(statement);
                	break;

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

    private PlayerProfile loadFromResult(String playerName, ResultSet result) throws SQLException {
        Map<SkillType, Integer> skills = new HashMap<SkillType, Integer>(); // Skill & Level
        Map<SkillType, Float> skillsXp = new HashMap<SkillType, Float>(); // Skill & XP
        Map<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown
        MobHealthbarType mobHealthbarType;
        UUID uuid;
        
        int skillOffset = 0;
        int xpOffset = skillOffset + SkillType.getNonChildSkills().size();
        int datsOffset = xpOffset + SkillType.getNonChildSkills().size();
        int otherOffset = datsOffset + AbilityType.getAbilities().size();

        SkillType skill;
        for(int i = 0; i < SkillType.getNonChildSkills().size(); i++) {
        	skill = SkillType.getNonChildSkills().get(i);
        	skills.put(skill, result.getInt(skillOffset + i + 1));
        	skillsXp.put(skill, result.getFloat(xpOffset + i + 1));
        }

        AbilityType ability;
        for(int i = 0; i < AbilityType.getAbilities().size(); i++) {
        	ability = AbilityType.getAbilities().get(i);
        	skillsDATS.put(ability, result.getInt(datsOffset + i + 1));
        }

        try {
            mobHealthbarType = MobHealthbarType.valueOf(result.getString(otherOffset + 2));
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        try {
            uuid = UUID.fromString(result.getString(otherOffset + 3));
        }
        catch (Exception e) {
            uuid = null;
        }

        return new PlayerProfile(playerName, uuid, skills, skillsXp, skillsDATS, mobHealthbarType);
    }

    private void printErrors(SQLException ex) {
        StackTraceElement element = ex.getStackTrace()[0];
        mcMMO.p.getLogger().severe("Location: " + element.getClassName() + " " + element.getMethodName() + " " + element.getLineNumber());
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

            if (resultSet.getRow() != SkillType.getNonChildSkills().size()) {
                mcMMO.p.getLogger().info("Indexing tables, this may take a while on larger databases");

                for (SkillType skill : SkillType.getNonChildSkills()) {
                    String skill_name = skill.getName().toLowerCase();

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

        new GetUUIDUpdatesRequired().runTaskLaterAsynchronously(mcMMO.p, 100); // wait until after first purge
    }
    
    private void checkUpgradeChangeCooldownNames(final Statement statement) throws SQLException {
        try {
            statement.executeQuery("SELECT `" + AbilityType.getLowerAbilitieNames().get(0) + "` FROM `" + tablePrefix + "cooldowns` LIMIT 1");
        }
        catch (SQLException ex) {
            mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for changed MySQL ability cooldown names...");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `mining` `super_breaker` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `woodcutting` `tree_feller` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `unarmed` `berserk` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `herbalism` `green_terra` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `excavation` `giga_drill_breaker` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `swords` `serrated_strikes` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` CHANGE `axes` `skull_splitter` int(32) unsigned NOT NULL DEFAULT '0'");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` DROP `taming`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` DROP `repair`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` DROP `archery`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "cooldowns` DROP `acrobatics`");
        }
    }

    private class GetUUIDUpdatesRequired extends BukkitRunnable {
        public void run() {
            massUpdateLock.lock();
            List<String> names = new ArrayList<String>();
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                try {
                    connection = miscPool.getConnection();
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery("SELECT `user` FROM `" + tablePrefix + "users` WHERE `uuid` IS NULL");

                    while (resultSet.next()) {
                        names.add(resultSet.getString("user"));
                    }
                } catch (SQLException ex) {
                    printErrors(ex);
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            // Ignore
                        }
                    }
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            // Ignore
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            // Ignore
                        }
                    }
                }

                if (!names.isEmpty()) {
                    new UUIDUpdateAsyncTask(mcMMO.p, names).run();;
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
    
    private void addNewSkills(Connection connection) {
        Statement statement = null;

        try {
            statement = connection.createStatement();
            for (String skill : SkillType.getLowerSkillNames()) {
                try {
                    statement.executeQuery("SELECT `" + skill + "` FROM `" + tablePrefix + "skills` LIMIT 1");
                }
                catch (SQLException ex) {
                    mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Fishing...");
                    statement.executeUpdate("ALTER TABLE `" + tablePrefix + "skills` ADD `" + skill + "` int(10) NOT NULL DEFAULT '0'");
                    statement.executeUpdate("ALTER TABLE `" + tablePrefix + "experience` ADD `" + skill + "` int(10) NOT NULL DEFAULT '0'");
                }
            }
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

    private int getUserID(final Connection connection, final String playerName, final UUID uuid) {
        if (cachedUserIDs.containsKey(uuid)) {
            return cachedUserIDs.get(uuid);
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT id, user FROM " + tablePrefix + "users WHERE uuid = ? OR (uuid IS NULL AND user = ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");

                cachedUserIDs.put(uuid, id);

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

	private static String getAllQueryVersion() {
		return org.apache.commons.lang.StringUtils.join(SkillType.getLowerSkillNames(), '+');
	}

	@Override
    public void onDisable() {
        mcMMO.p.debug("Releasing connection pool resource...");
        miscPool.release();
        loadPool.release();
        savePool.release();
    }

    public enum PoolIdentifier {
        MISC,
        LOAD,
        SAVE;
    }
}
