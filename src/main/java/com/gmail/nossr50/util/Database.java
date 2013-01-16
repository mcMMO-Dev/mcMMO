package com.gmail.nossr50.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.DatabaseUpdate;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.SpoutHud;
import com.gmail.nossr50.runnables.SQLReconnect;
import com.gmail.nossr50.spout.SpoutStuff;

public class Database {

    private static Config configInstance = Config.getInstance();
    private static String connectionString;

    private static String tablePrefix = configInstance.getMySQLTablePrefix();
    private static Connection connection = null;
    private static mcMMO plugin = null;

    // Scale waiting time by this much per failed attempt
    private static final double SCALING_FACTOR = 40;

    // Minimum wait in nanoseconds (default 500ms)
    private static final long MIN_WAIT = 500L*1000000L;

    // Maximum time to wait between reconnects (default 5 minutes)
    private static final long MAX_WAIT = 5L * 60L * 1000L * 1000000L;

    // How long to wait when checking if connection is valid (default 3 seconds)
    private static final int VALID_TIMEOUT = 3;

    // When next to try connecting to Database in nanoseconds
    private static long nextReconnectTimestamp = 0L;

    // How many connection attemtps have failed
    private static int reconnectAttempt = 0;

    public Database(mcMMO instance) {
        plugin = instance;
        checkConnected(); //Connect to MySQL
    }

    /**
     * Attempt to connect to the mySQL database.
     */
    public static void connect() {
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
        } catch (SQLException ex) {
            connection = null;
            if (reconnectAttempt == 0 || reconnectAttempt >= 11) mcMMO.p.getLogger().info("Connection to MySQL failed!");
        } catch (ClassNotFoundException ex) {
            connection = null;
            if (reconnectAttempt == 0 || reconnectAttempt >= 11) mcMMO.p.getLogger().info("MySQL database driver not found!");
        }
    }

    /**
     * Attempt to create the database structure.
     */
    public void createStructure() {
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "users` ("
                + "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`user` varchar(40) NOT NULL,"
                + "`lastlogin` int(32) unsigned NOT NULL,"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `user` (`user`)) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "huds` ("
                + "`user_id` int(10) unsigned NOT NULL,"
                + "`hudtype` varchar(50) NOT NULL DEFAULT 'STANDARD',"
                + "PRIMARY KEY (`user_id`),"
                + "FOREIGN KEY (`user_id`) REFERENCES `" + tablePrefix + "users` (`id`) "
                + "ON DELETE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
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
                + "PRIMARY KEY (`user_id`),"
                + "FOREIGN KEY (`user_id`) REFERENCES `" + tablePrefix + "users` (`id`) "
                + "ON DELETE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
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
                + "PRIMARY KEY (`user_id`),"
                + "FOREIGN KEY (`user_id`) REFERENCES `" + tablePrefix + "users` (`id`) "
                + "ON DELETE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
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
                + "PRIMARY KEY (`user_id`),"
                + "FOREIGN KEY (`user_id`) REFERENCES `" + tablePrefix + "users` (`id`) "
                + "ON DELETE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1;");

        checkDatabaseStructure(DatabaseUpdate.FISHING);
        checkDatabaseStructure(DatabaseUpdate.BLAST_MINING);
        checkDatabaseStructure(DatabaseUpdate.CASCADE_DELETE);
    }

    /**
     * Check database structure for missing values.
     *
     * @param update Type of data to check updates for  
     */
    public void checkDatabaseStructure(DatabaseUpdate update) {
        String sql = null;
        ResultSet resultSet = null;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        switch (update) {
        case BLAST_MINING:
            sql = "SELECT * FROM  `" + tablePrefix + "cooldowns` ORDER BY  `" + tablePrefix + "cooldowns`.`blast_mining` ASC LIMIT 0 , 30";
            break;

        case CASCADE_DELETE:
            write("ALTER TABLE `" + tablePrefix + "huds` ADD FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;");
            write("ALTER TABLE `" + tablePrefix + "experience` ADD FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;");
            write("ALTER TABLE `" + tablePrefix + "cooldowns` ADD FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;");
            write("ALTER TABLE `" + tablePrefix + "skills` ADD FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;");

        case FISHING:
            sql = "SELECT * FROM  `" + tablePrefix + "experience` ORDER BY  `" + tablePrefix + "experience`.`fishing` ASC LIMIT 0 , 30";
            break;

        default:
            break;
        }

        PreparedStatement statement = null;
        try {
            if (!checkConnected()) return;
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

            default:
                break;
            }
        } finally {
            if (resultSet != null) {
                try     {
                    resultSet.close();
                } catch (SQLException e) {
                    // Ignore the error, we're leaving
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // Ignore the error, we're leaving
                }
            }
        }
    }

    /**
     * Attempt to write the SQL query.
     *
     * @param sql Query to write.
     * @return true if the query was successfully written, false otherwise.
     */
    public boolean write(String sql) {
        if (checkConnected()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                statement.close();
                return true;
            }
            catch (SQLException ex) {
                printErrors(ex);
                return false;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        printErrors(e);
                        return false;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns the number of rows affected by either a DELETE or UPDATE query
     *
     * @param sql SQL query to execute
     * @return the number of rows affected
     */
    public int update(String sql) {
        int ret = 0;
        if (checkConnected()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                ret = statement.executeUpdate();
                statement.close();
                return ret;
            } catch (SQLException ex) {
                printErrors(ex);
                return 0;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        printErrors(e);
                        return 0;
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Get the Integer. Only return first row / first field.
     *
     * @param sql SQL query to execute
     * @return the value in the first row / first field
     */
    public int getInt(String sql) {
        ResultSet resultSet;
        int result = 0;

        if (checkConnected()) {
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
                else {
                    result = 0;
                }

                statement.close();
            }
            catch (SQLException ex) {
                printErrors(ex);
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
            } catch (SQLException e) {
                isClosed = true;
                e.printStackTrace();
                printErrors(e);
            }

            if (!isClosed) {
                try {
                    isValid = connection.isValid(VALID_TIMEOUT);
                } catch (SQLException e) {
                    // Don't print stack trace because it's valid to lose idle connections
                    // to the server and have to restart them.
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
            } catch (SQLException ex) {
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
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SQLReconnect(plugin), 5);
                }
                nextReconnectTimestamp = 0;
                reconnectAttempt = 0;
                return true;
            }
        } catch (SQLException e) {
            // Failed to check isClosed, so presume connection is bad and attempt later
            e.printStackTrace();
            printErrors(e);
        }

        reconnectAttempt++;

        nextReconnectTimestamp = (long)(System.nanoTime() + Math.min(MAX_WAIT, (reconnectAttempt*SCALING_FACTOR*MIN_WAIT)));

        return false;
    }

    /**
     * Read SQL query.
     *
     * @param sql SQL query to read
     * @return the rows in this SQL query
     */
    public HashMap<Integer, ArrayList<String>> read(String sql) {
        ResultSet resultSet;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        if (checkConnected()) {
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    ArrayList<String> column = new ArrayList<String>();

                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        column.add(resultSet.getString(i));
                    }

                    rows.put(resultSet.getRow(), column);
                }

                statement.close();
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
        }

        return rows;
    }
    
    public Map<String, Integer> readSQLRank(String playerName) {
        ResultSet resultSet;
        Map<String, Integer> skills = new HashMap<String, Integer>();
        if (checkConnected()) {
            try {
                String sql = "SELECT "
                        + "(SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'ALL'" 
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.fishing desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'FISHING'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.taming desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'TAMING'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.woodcutting desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'WOODCUTTING'" 
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.repair desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'REPAIR'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.unarmed desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'UNARMED'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.herbalism desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'HERBALISM'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.excavation desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'EXCAVATION'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.archery desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'ARCHERY'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.swords desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'SWORDS'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.axes desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'AXES'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.acrobatics desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'ACROBATICS'"
                        + ", (SELECT rank FROM (SELECT @rownum:=@rownum+1 rank, p.user AS user FROM (SELECT @rownum:=0) AS rank, ((SELECT u.user AS user FROM " + tablePrefix + "users u, " + tablePrefix + "skills s WHERE u.id = s.user_id ORDER BY s.mining desc) AS p)) AS d WHERE user = '" + playerName + "') AS 'MINING'";
                PreparedStatement statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    for (SkillType skillType: SkillType.values()) {
                        skills.put(skillType.name(), resultSet.getInt(skillType.name()));
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

    public void purgePowerlessSQL() {
        plugin.getLogger().info("Purging powerless users...");
        HashMap<Integer, ArrayList<String>> usernames = read("SELECT u.user FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0");
        write("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.id IN (SELECT * FROM (SELECT u.id FROM " + tablePrefix + "skills AS s, " + tablePrefix + "users AS u WHERE s.user_id = u.id AND (s.taming+s.mining+s.woodcutting+s.repair+s.unarmed+s.herbalism+s.excavation+s.archery+s.swords+s.axes+s.acrobatics+s.fishing) = 0) AS p)");

        int purgedUsers = 0;
        for (int i = 1; i <= usernames.size(); i++) {
            String playerName = usernames.get(i).get(0);

            if (playerName == null || Bukkit.getOfflinePlayer(playerName).isOnline()) {
                continue;
            }

            profileCleanup(playerName);
            purgedUsers++;
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    public void purgeOldSQL() {
        plugin.getLogger().info("Purging old users...");
        long currentTime = System.currentTimeMillis();
        long purgeTime = 2630000000L * Config.getInstance().getOldUsersCutoff();
        HashMap<Integer, ArrayList<String>> usernames = read("SELECT user FROM " + tablePrefix + "users WHERE ((" + currentTime + " - lastlogin*1000) > " + purgeTime + ")");
        write("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.id IN (SELECT * FROM (SELECT id FROM " + tablePrefix + "users WHERE ((" + currentTime + " - lastlogin*1000) > " + purgeTime + ")) AS p)");

        int purgedUsers = 0;
        for (int i = 1; i <= usernames.size(); i++) {
            String playerName = usernames.get(i).get(0);

            if (playerName == null) {
                continue;
            }

            profileCleanup(playerName);
            purgedUsers++;
        }

        plugin.getLogger().info("Purged " + purgedUsers + " users from the database.");
    }

    private static void printErrors(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    public static void profileCleanup(String playerName) {
        McMMOPlayer mcmmoPlayer = Users.getPlayer(playerName);

        if (mcmmoPlayer != null) {
            Player player = mcmmoPlayer.getPlayer();
            SpoutHud spoutHud = mcmmoPlayer.getProfile().getSpoutHud();

            if (spoutHud != null) {
                spoutHud.removeWidgets();
            }

            Users.remove(playerName);

            if (player.isOnline()) {
                Users.addUser(player);

                if (mcMMO.spoutEnabled) {
                    SpoutStuff.reloadSpoutPlayer(player);
                }
            }
        }
    }
}
