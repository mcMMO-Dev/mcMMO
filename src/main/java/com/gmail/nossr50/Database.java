package com.gmail.nossr50;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.DatabaseUpdate;
import com.gmail.nossr50.datatypes.PlayerProfile;

public class Database {

    private String connectionString = "jdbc:mysql://" + LoadProperties.MySQLserverName + ":" + LoadProperties.MySQLport + "/" + LoadProperties.MySQLdbName + "?user=" + LoadProperties.MySQLuserName + "&password=" + LoadProperties.MySQLdbPass;
    private Connection conn = null;
    private mcMMO plugin = null;
    private long reconnectTimestamp = 0;

    public Database(mcMMO instance) {
        plugin = instance;
        connect(); //Connect to MySQL

        // Load the driver instance
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.getConnection(connectionString);
        }
        catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning(e.getLocalizedMessage());
        }
        catch (SQLException ex) {
            Bukkit.getLogger().warning(ex.getLocalizedMessage());
            printErrors(ex);
        }
    }

    /**
     * Attempt to connect to the mySQL database.
     */
    public void connect() {
        try {
            System.out.println("[mcMMO] Attempting connection to MySQL...");
            Properties conProperties = new Properties();
            conProperties.put("autoReconnect", "false");
            conProperties.put("maxReconnects", "0");
            conn = DriverManager.getConnection(connectionString, conProperties);
            System.out.println("[mcMMO] Connection to MySQL was a success!");
        }
        catch (SQLException ex) {
            System.out.println("[mcMMO] Connection to MySQL failed!");
            ex.printStackTrace();
            printErrors(ex);
        }
    }

    /**
     * Attempt to create the database structure.
     */
    public void createStructure() {
        write("CREATE TABLE IF NOT EXISTS `" + LoadProperties.MySQLtablePrefix + "huds` (`user_id` int(10) unsigned NOT NULL,"
                + "`hudtype` varchar(50) NOT NULL DEFAULT '',"
                + "PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        write("CREATE TABLE IF NOT EXISTS `" + LoadProperties.MySQLtablePrefix + "users` (`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
                + "`user` varchar(40) NOT NULL,"
                + "`lastlogin` int(32) unsigned NOT NULL,"
                + "`party` varchar(100) NOT NULL DEFAULT '',"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `user` (`user`)) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        write("CREATE TABLE IF NOT EXISTS `" + LoadProperties.MySQLtablePrefix + "cooldowns` (`user_id` int(10) unsigned NOT NULL,"
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
                + "PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        write("CREATE TABLE IF NOT EXISTS `" + LoadProperties.MySQLtablePrefix + "skills` (`user_id` int(10) unsigned NOT NULL,"
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
                + "PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
        write("CREATE TABLE IF NOT EXISTS `" + LoadProperties.MySQLtablePrefix + "experience` (`user_id` int(10) unsigned NOT NULL,"
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
                + "PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");

        checkDatabaseStructure(DatabaseUpdate.FISHING);
        checkDatabaseStructure(DatabaseUpdate.BLAST_MINING);
    }

    /**
     * Check database structure for missing values.
     *
     * @param update Type of data to check updates for
     */
    public void checkDatabaseStructure(DatabaseUpdate update) {
        String sql = null;
        ResultSet rs = null;
        HashMap<Integer, ArrayList<String>> Rows = new HashMap<Integer, ArrayList<String>>();

        switch (update) {
        case BLAST_MINING:
            sql = "SELECT * FROM  `"+LoadProperties.MySQLtablePrefix+"cooldowns` ORDER BY  `"+LoadProperties.MySQLtablePrefix+"cooldowns`.`blast_mining` ASC LIMIT 0 , 30";
            break;
        case FISHING:
            sql = "SELECT * FROM  `"+LoadProperties.MySQLtablePrefix+"experience` ORDER BY  `"+LoadProperties.MySQLtablePrefix+"experience`.`fishing` ASC LIMIT 0 , 30";
            break;
        default:
            break;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (stmt.executeQuery() != null) {
                stmt.executeQuery();
                rs = stmt.getResultSet();
                while (rs.next()) {
                    ArrayList<String> Col = new ArrayList<String>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Col.add(rs.getString(i));
                    }
                    Rows.put(rs.getRow(), Col);
                }
            }
        }
        catch (SQLException ex) {
            if (update.equals(DatabaseUpdate.BLAST_MINING)) {
                System.out.println("Updating mcMMO MySQL tables for Blast Mining...");
                write("ALTER TABLE `"+LoadProperties.MySQLtablePrefix + "cooldowns` ADD `blast_mining` int(32) NOT NULL DEFAULT '0' ;");
            }
            else if (update.equals(DatabaseUpdate.FISHING)) {
                System.out.println("Updating mcMMO MySQL tables for Fishing...");
                write("ALTER TABLE `"+LoadProperties.MySQLtablePrefix + "skills` ADD `fishing` int(10) NOT NULL DEFAULT '0' ;");
                write("ALTER TABLE `"+LoadProperties.MySQLtablePrefix + "experience` ADD `fishing` int(10) NOT NULL DEFAULT '0' ;");
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
        if (isConnected()) {
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();
                return true;
            }
            catch (SQLException ex) {
                printErrors(ex);
                return false;
            }
        }
        else {
            attemptReconnect();
        }
        return false;
    }

    /**
     * Get the Integer. Only return first row / first field.
     *
     * @param sql SQL query to execute
     * @return the value in the first row / first field
     */
    public Integer getInt(String sql) {
        ResultSet rs = null;
        Integer result = 0;

        if (isConnected()) {
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt = conn.prepareStatement(sql);
                if (stmt.executeQuery() != null) {
                    stmt.executeQuery();
                    rs = stmt.getResultSet();
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
                    else {
                        result = 0;
                    }
                }
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
        }
        else {
            attemptReconnect();
        }
        return result;
    }
    
    /**
     * Get connection status
     * 
     * @return the boolean value for whether or not we are connected
     */
    public boolean isConnected() {
        if(conn == null)
            return false;
        
        try {
            return conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Schedules a Sync Delayed Task with the Bukkit Scheduler to attempt reconnection after a minute has elapsed
     * This will check for a connection being present or not to prevent unneeded reconnection attempts
     */
    public void attemptReconnect() {
        if(reconnectTimestamp+60000 < System.currentTimeMillis()) //Only reconnect if another attempt hasn't been made recently
        {
            System.out.println("[mcMMO] Connection to MySQL was lost! Attempting to reconnect in 60 seconds...");
            reconnectTimestamp = System.currentTimeMillis();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,     
            new Runnable() {
                public void run() {
                    if (!isConnected()) {
                        connect();
                        if(isConnected()) {
                            for(PlayerProfile x : Users.players.values()) {
                                x.save(); //Save all profiles
                            }
                            Users.players.clear(); //Clear the profiles
                            for(Player x : Bukkit.getOnlinePlayers()) {
                                Users.addUser(x); //Add in new profiles, forcing them to 'load' again from MySQL
                            }
                        }
                    }
                }
            }, 20*60);
        }
    }

    /**
     * Read SQL query.
     *
     * @param sql SQL query to read
     * @return the rows in this SQL query
     */
    public HashMap<Integer, ArrayList<String>> read(String sql) {
        ResultSet rs = null;
        HashMap<Integer, ArrayList<String>> Rows = new HashMap<Integer, ArrayList<String>>();

        if (isConnected()) {
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                if (stmt.executeQuery() != null) {
                    stmt.executeQuery();
                    rs = stmt.getResultSet();
                    while (rs.next()) {
                        ArrayList<String> Col = new ArrayList<String>();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            Col.add(rs.getString(i));
                        }
                        Rows.put(rs.getRow(), Col);
                    }
                }
            }
            catch (SQLException ex) {
                printErrors(ex);
            }
        }
        else {
            attemptReconnect();
        }
        return Rows;
    }

    private static void printErrors(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}