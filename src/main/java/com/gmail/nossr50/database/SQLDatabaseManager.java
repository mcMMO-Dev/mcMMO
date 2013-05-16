package com.gmail.nossr50.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.database.DatabaseUpdateType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.runnables.database.SQLReconnectTask;
import com.gmail.nossr50.util.Misc;

public final class SQLDatabaseManager {
    private static String connectionString;

    private static String tablePrefix = Config.getInstance().getMySQLTablePrefix();
    private static Connection connection = null;

    // Scale waiting time by this much per failed attempt
    private static final double SCALING_FACTOR = 40;

    // Minimum wait in nanoseconds (default 500ms)
    private static final long MIN_WAIT = 500L * 1000000L;

    // Maximum time to wait between reconnects (default 5 minutes)
    private static final long MAX_WAIT = 5L * 60L * 1000L * 1000000L;

    // How long to wait when checking if connection is valid (default 3 seconds)
    private static final int VALID_TIMEOUT = 3;

    // When next to try connecting to Database in nanoseconds
    private static long nextReconnectTimestamp = 0L;

    // How many connection attempts have failed
    private static int reconnectAttempt = 0;

    private static final long ONE_MONTH = 2630000000L;

    private SQLDatabaseManager() {}

    /**
     * Attempt to connect to the mySQL database.
     */
    public static void connect() {
        Config configInstance = Config.getInstance();
        connectionString = "jdbc:mysql://" + configInstance.getMySQLServerName() + ":" + configInstance.getMySQLServerPort() + "/" + configInstance.getMySQLDatabaseName();

        try {
            mcMMO.p.getLogger().info("Attempting connection to MySQL...");

            // Force driver to load if not yet loaded
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", configInstance.getMySQLUserName());
            connectionProperties.put("password", configInstance.getMySQLUserPassword());
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("maxReconnects", "0");
            connection = DriverManager.getConnection(connectionString, connectionProperties);

            mcMMO.p.getLogger().info("Connection to MySQL was a success!");
        }
        catch (SQLException ex) {
            connection = null;

            if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
                mcMMO.p.getLogger().info("Connection to MySQL failed!");
            }
        }
        catch (ClassNotFoundException ex) {
            connection = null;

            if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
                mcMMO.p.getLogger().info("MySQL database driver not found!");
            }
        }
    }

    /**
     * Attempt to create the database structure.
     */
    public static void createStructure() {
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` ("
                + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`user` varchar(40) NOT NULL,"
                + "`lastlogin` int(32) unsigned NOT NULL,"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `user` (`user`)) DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "huds` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`hudtype` varchar(50) NOT NULL DEFAULT 'STANDARD',"
                + "`mobhealthbar` varchar(50) NOT NULL DEFAULT '" + Config.getInstance().getMobHealthbarDefault() + "',"
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=latin1;");
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "cooldowns` ("
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
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "skills` ("
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
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=latin1;");
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "experience` ("
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
                + "PRIMARY KEY (`user_id`)) "
                + "DEFAULT CHARSET=latin1;");

        checkDatabaseStructure(DatabaseUpdateType.FISHING);
        checkDatabaseStructure(DatabaseUpdateType.BLAST_MINING);
        checkDatabaseStructure(DatabaseUpdateType.INDEX);
        checkDatabaseStructure(DatabaseUpdateType.MOB_HEALTHBARS);
        checkDatabaseStructure(DatabaseUpdateType.PARTY_NAMES);
        checkDatabaseStructure(DatabaseUpdateType.KILL_ORPHANS);
    }

    /**
     * Attempt to write the SQL query.
     *
     * @param sql Query to write.
     * @return true if the query was successfully written, false otherwise.
     */
    public static boolean write(String sql) {
        if (!checkConnected()) {
            return false;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            return true;
        }
        catch (SQLException ex) {
            if (!sql.equalsIgnoreCase("ALTER TABLE `" + tablePrefix + "users` DROP COLUMN `party` ;")) {
                printErrors(ex);
            }
            return false;
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    printErrors(e);
                }
            }
        }
    }

    public static boolean removeUserSQL(String playerName) {
        return SQLDatabaseManager.update("DELETE FROM u, e, h, s, c " +
                "USING " + tablePrefix + "users u " +
                "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                "WHERE u.user = '" + playerName + "'") != 0;
    }

    /**
     * Returns the number of rows affected by either a DELETE or UPDATE query
     *
     * @param sql SQL query to execute
     * @return the number of rows affected
     */
    public static int update(String sql) {
        if (!checkConnected()) {
            return 0;
        }

        int rows = 0;

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            rows = statement.executeUpdate();
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
                    printErrors(e);
                }
            }
        }

        return rows;
    }

    /**
     * Get the Integer. Only return first row / first field.
     *
     * @param sql SQL query to execute
     * @return the value in the first row / first field
     */
    public static int getInt(String sql) {
        if (!checkConnected()) {
            return 0;
        }

        int result = 0;

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt(1);
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
                    printErrors(e);
                }
            }
        }

        return result;
    }

    /**
     * Check connection status and re-establish if dead or stale.
     *
     * If the very first immediate attempt fails, further attempts
     * will be made in progressively larger intervals up to MAX_WAIT
     * intervals.
     *
     * This allows for MySQL to time out idle connections as needed by
     * server operator, without affecting McMMO, while still providing
     * protection against a database outage taking down Bukkit's tick
     * processing loop due to attemping a database connection each
     * time McMMO needs the database.
     *
     * @return the boolean value for whether or not we are connected
     */
    public static boolean checkConnected() {
        boolean isClosed = true;
        boolean isValid = false;
        boolean exists = (connection != null);

        // If we're waiting for server to recover then leave early
        if (nextReconnectTimestamp > 0 && nextReconnectTimestamp > System.nanoTime()) {
            return false;
        }

        if (exists) {
            try {
                isClosed = connection.isClosed();
            }
            catch (SQLException e) {
                isClosed = true;
                e.printStackTrace();
                printErrors(e);
            }

            if (!isClosed) {
                try {
                    isValid = connection.isValid(VALID_TIMEOUT);
                }
                catch (SQLException e) {
                    // Don't print stack trace because it's valid to lose idle connections to the server and have to restart them.
                    isValid = false;
                }
            }
        }

        // Leave if all ok
        if (exists && !isClosed && isValid) {
            // Housekeeping
            nextReconnectTimestamp = 0;
            reconnectAttempt = 0;
            return true;
        }

        // Cleanup after ourselves for GC and MySQL's sake
        if (exists && !isClosed) {
            try {
                connection.close();
            }
            catch (SQLException ex) {
                // This is a housekeeping exercise, ignore errors
            }
        }

        // Try to connect again
        connect();

        // Leave if connection is good
        try {
            if (connection != null && !connection.isClosed()) {
                // Schedule a database save if we really had an outage
                if (reconnectAttempt > 1) {
                    new SQLReconnectTask().runTaskLater(mcMMO.p, 5);
                }
                nextReconnectTimestamp = 0;
                reconnectAttempt = 0;
                return true;
            }
        }
        catch (SQLException e) {
            // Failed to check isClosed, so presume connection is bad and attempt later
            e.printStackTrace();
            printErrors(e);
        }

        reconnectAttempt++;
        nextReconnectTimestamp = (long)(System.nanoTime() + Math.min(MAX_WAIT, (reconnectAttempt * SCALING_FACTOR * MIN_WAIT)));
        return false;
    }

    /**
     * Read SQL query.
     *
     * @param sql SQL query to read
     * @return the rows in this SQL query
     */
    public static HashMap<Integer, ArrayList<String>> read(String sql) {
        ResultSet resultSet;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        if (checkConnected()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    ArrayList<String> column = new ArrayList<String>();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        column.add(resultSet.getString(i));
                    }

                    rows.put(resultSet.getRow(), column);
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
                        printErrors(e);
                    }
                }
            }
        }

        return rows;
    }

    public static Map<String, Integer> readSQLRank(String playerName) {
        ResultSet resultSet;
        Map<String, Integer> skills = new HashMap<String, Integer>();

        if (checkConnected()) {
            try {
                for (SkillType skillType : SkillType.values()) {
                    if (skillType.isChildSkill()) {
                        continue;
                    }

                    String skillName = skillType.name().toLowerCase();
                    String sql = "SELECT COUNT(*) AS rank FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                                 "AND " + skillName + " > (SELECT " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                                 "WHERE user = ?)";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, playerName);
                    resultSet = statement.executeQuery();

                    resultSet.next();

                    int rank = resultSet.getInt("rank");

                    sql = "SELECT user, " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE " + skillName + " > 0 " +
                          "AND " + skillName + " = (SELECT " + skillName + " FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                          "WHERE user = '" + playerName + "') ORDER BY user";

                    statement = connection.prepareStatement(sql);
                    resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        if (resultSet.getString("user").equalsIgnoreCase(playerName)) {
                            skills.put(skillType.name(), rank + resultSet.getRow());
                            break;
                        }
                    }

                    statement.close();
                }

                String sql = "SELECT COUNT(*) AS rank FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                             "WHERE taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing > 0 " +
                             "AND taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing > " +
                             "(SELECT taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing " +
                             "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE user = ?)";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                resultSet = statement.executeQuery();

                resultSet.next();

                int rank = resultSet.getInt("rank");

                sql = "SELECT user, taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing " +
                      "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id " +
                      "WHERE taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing > 0 " +
                      "AND taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing = " +
                      "(SELECT taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing " +
                      "FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON user_id = id WHERE user = ?) ORDER BY user";

                statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getString("user").equalsIgnoreCase(playerName)) {
                        skills.put("ALL", rank + resultSet.getRow());
                        break;
                    }
                }

                statement.close();
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
        }

        return skills;
    }

    public static int purgePowerlessSQL() {
        HashMap<Integer, ArrayList<String>> usernames = read("SELECT u.user FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");
        write("DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                "WHERE (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");
        return processPurge(usernames.values());
    }

    public static int purgeOldSQL() {
        long currentTime = System.currentTimeMillis();
        long purgeTime = ONE_MONTH * Config.getInstance().getOldUsersCutoff();

        HashMap<Integer, ArrayList<String>> usernames = read("SELECT user FROM " + tablePrefix + "users WHERE ((" + currentTime + " - lastlogin*1000) > " + purgeTime + ")");
        write("DELETE FROM u, e, h, s, c USING " + tablePrefix + "users u " +
                "JOIN " + tablePrefix + "experience e ON (u.id = e.user_id) " +
                "JOIN " + tablePrefix + "huds h ON (u.id = h.user_id) " +
                "JOIN " + tablePrefix + "skills s ON (u.id = s.user_id) " +
                "JOIN " + tablePrefix + "cooldowns c ON (u.id = c.user_id) " +
                "WHERE ((" + currentTime + " - lastlogin*1000) > " + purgeTime + ")");

        return processPurge(usernames.values());
    }

    private static int processPurge(Collection<ArrayList<String>> usernames) {
        int purgedUsers = 0;

        for (ArrayList<String> user : usernames) {
            Misc.profileCleanup(user.get(0));
            purgedUsers++;
        }

        return purgedUsers;
    }

    /**
     * Check database structure for missing values.
     *
     * @param update Type of data to check updates for
     */
    private static void checkDatabaseStructure(DatabaseUpdateType update) {
        String sql = null;
        ResultSet resultSet = null;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        switch (update) {
            case BLAST_MINING:
                sql = "SELECT * FROM  `" + tablePrefix + "cooldowns` ORDER BY  `" + tablePrefix + "cooldowns`.`blast_mining` ASC LIMIT 0 , 30";
                break;

            case FISHING:
                sql = "SELECT * FROM  `" + tablePrefix + "experience` ORDER BY  `" + tablePrefix + "experience`.`fishing` ASC LIMIT 0 , 30";
                break;

            case INDEX:
                if (read("SHOW INDEX FROM " + tablePrefix + "skills").size() != 13 && checkConnected()) {
                    mcMMO.p.getLogger().info("Indexing tables, this may take a while on larger databases");
                    write("ALTER TABLE `" + tablePrefix + "skills` ADD INDEX `idx_taming` (`taming`) USING BTREE, "
                            + "ADD INDEX `idx_mining` (`mining`) USING BTREE, "
                            + "ADD INDEX `idx_woodcutting` (`woodcutting`) USING BTREE, "
                            + "ADD INDEX `idx_repair` (`repair`) USING BTREE, "
                            + "ADD INDEX `idx_unarmed` (`unarmed`) USING BTREE, "
                            + "ADD INDEX `idx_herbalism` (`herbalism`) USING BTREE, "
                            + "ADD INDEX `idx_excavation` (`excavation`) USING BTREE, "
                            + "ADD INDEX `idx_archery` (`archery`) USING BTREE, "
                            + "ADD INDEX `idx_swords` (`swords`) USING BTREE, "
                            + "ADD INDEX `idx_axes` (`axes`) USING BTREE, "
                            + "ADD INDEX `idx_acrobatics` (`acrobatics`) USING BTREE, "
                            + "ADD INDEX `idx_fishing` (`fishing`) USING BTREE;");
                }
                return;

            case MOB_HEALTHBARS:
                sql = "SELECT * FROM  `" + tablePrefix + "huds` ORDER BY  `" + tablePrefix + "huds`.`mobhealthbar` ASC LIMIT 0 , 30";
                break;

            case PARTY_NAMES:
                write("ALTER TABLE `" + tablePrefix + "users` DROP COLUMN `party` ;");
                return;

            case KILL_ORPHANS:
                mcMMO.p.getLogger().info("Killing orphans");
                write(
                        "DELETE FROM " + tablePrefix + "experience " +
                         "WHERE NOT EXISTS (SELECT * FROM " +
                         tablePrefix + "users u WHERE " + 
                         tablePrefix + "experience.user_id = u.id);" 
                         );
                write(
                        "DELETE FROM " + tablePrefix + "huds " +
                         "WHERE NOT EXISTS (SELECT * FROM " +
                         tablePrefix + "users u WHERE " + 
                         tablePrefix + "huds.user_id = u.id);" 
                         );
                write(
                        "DELETE FROM " + tablePrefix + "cooldowns " +
                         "WHERE NOT EXISTS (SELECT * FROM " +
                         tablePrefix + "users u WHERE " + 
                         tablePrefix + "cooldowns.user_id = u.id);" 
                         );
                write(
                        "DELETE FROM " + tablePrefix + "skills " +
                         "WHERE NOT EXISTS (SELECT * FROM " +
                         tablePrefix + "users u WHERE " + 
                         tablePrefix + "skills.user_id = u.id);" 
                         );
                return;

            default:
                break;
        }

        PreparedStatement statement = null;
        try {
            if (!checkConnected()) {
                return;
            }

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ArrayList<String> column = new ArrayList<String>();

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    column.add(resultSet.getString(i));
                }

                rows.put(resultSet.getRow(), column);
            }
        }
        catch (SQLException ex) {
            switch (update) {
                case BLAST_MINING:
                    mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Blast Mining...");
                    write("ALTER TABLE `"+tablePrefix + "cooldowns` ADD `blast_mining` int(32) NOT NULL DEFAULT '0' ;");
                    break;

                case FISHING:
                    mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for Fishing...");
                    write("ALTER TABLE `"+tablePrefix + "skills` ADD `fishing` int(10) NOT NULL DEFAULT '0' ;");
                    write("ALTER TABLE `"+tablePrefix + "experience` ADD `fishing` int(10) NOT NULL DEFAULT '0' ;");
                    break;

                case MOB_HEALTHBARS:
                    mcMMO.p.getLogger().info("Updating mcMMO MySQL tables for mob healthbars...");
                    write("ALTER TABLE `" + tablePrefix + "huds` ADD `mobhealthbar` varchar(50) NOT NULL DEFAULT '" + Config.getInstance().getMobHealthbarDefault() + "' ;");
                    break;

                default:
                    break;
            }
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    // Ignore the error, we're leaving
                }
            }
        }
    }

    private static void printErrors(SQLException ex) {
        mcMMO.p.getLogger().severe("SQLException: " + ex.getMessage());
        mcMMO.p.getLogger().severe("SQLState: " + ex.getSQLState());
        mcMMO.p.getLogger().severe("VendorError: " + ex.getErrorCode());
    }
}
