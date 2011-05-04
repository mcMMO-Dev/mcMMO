package com.gmail.nossr50;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.PreparedStatement;


import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import com.gmail.nossr50.config.LoadProperties;


public class Database {

	private Connection conn;
	private mcMMO plugin;
	
	public Database(mcMMO instance) 
	{
		plugin = instance;
		// Load the driver instance
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        	throw new DataSourceException("Failed to initialize JDBC driver");
        }
		
		// make the connection
		try {
		    conn = DriverManager.getConnection("jdbc:mysql://" + LoadProperties.MySQLserverName + ":" + LoadProperties.MySQLport + "/" + LoadProperties.MySQLdbName + "?user=" + LoadProperties.MySQLuserName + "&password=" + LoadProperties.MySQLdbPass);			
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	//Create the DB structure
	public void createStructure(){
		Write("CREATE TABLE IF NOT EXISTS `"+LoadProperties.MySQLtablePrefix+"users` (`id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`user` varchar(40) NOT NULL," +
				"`lastlogin` int(32) unsigned NOT NULL," +
				"`party` varchar(100) NOT NULL DEFAULT ''," +
				"PRIMARY KEY (`id`)," +
				"UNIQUE KEY `user` (`user`)) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
		Write("CREATE TABLE IF NOT EXISTS `"+LoadProperties.MySQLtablePrefix+"cooldowns` (`user_id` int(10) unsigned NOT NULL," +
				"`taming` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`mining` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`woodcutting` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`repair` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`unarmed` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`herbalism` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`excavation` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`archery` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`swords` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`axes` int(32) unsigned NOT NULL DEFAULT '0'," +
				"`acrobatics` int(32) unsigned NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
		Write("CREATE TABLE IF NOT EXISTS `"+LoadProperties.MySQLtablePrefix+"skills` (`user_id` int(10) unsigned NOT NULL," +
				"`taming` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`mining` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`woodcutting` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`repair` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`unarmed` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`herbalism` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`excavation` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`archery` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`swords` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`axes` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`acrobatics` int(10) unsigned NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
		Write("CREATE TABLE IF NOT EXISTS `"+LoadProperties.MySQLtablePrefix+"experience` (`user_id` int(10) unsigned NOT NULL," +
				"`taming` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`mining` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`woodcutting` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`repair` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`unarmed` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`herbalism` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`excavation` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`archery` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`swords` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`axes` int(10) unsigned NOT NULL DEFAULT '0'," +
				"`acrobatics` int(10) unsigned NOT NULL DEFAULT '0'," +
				"PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
		Write("CREATE TABLE IF NOT EXISTS `"+LoadProperties.MySQLtablePrefix+"spawn` (`user_id` int(10) NOT NULL," +
				"`x` int(64) NOT NULL DEFAULT '0'," +
				"`y` int(64) NOT NULL DEFAULT '0'," +
				"`z` int(64) NOT NULL DEFAULT '0'," +
				"`world` varchar(50) NOT NULL DEFAULT ''," +
				"PRIMARY KEY (`user_id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
	}
	// check if its closed
	private void reconnect()
	{
		System.out.println("[mcMMO] Reconnecting to MySQL...");
		try 
		{
		    conn = DriverManager.getConnection("jdbc:mysql://" + LoadProperties.MySQLserverName + ":" + LoadProperties.MySQLport + "/" + LoadProperties.MySQLdbName + "?user=" + LoadProperties.MySQLuserName + "&password=" + LoadProperties.MySQLdbPass);			
		    
		    System.out.println("[mcMMO] Connection success!");
		} catch (SQLException ex) 
		{
			System.out.println("[mcMMO] Connection to MySQL failed! Check status of MySQL server!");
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		try {
			if(conn.isValid(5)){
				Users.clearUsers();
				
				for(Player x : plugin.getServer().getOnlinePlayers())
				{
					Users.addUser(x);
				}
			}
		} catch (SQLException e) {
			//Herp
		}
	}
	// write query
	public boolean Write(String sql) 
	{
		/*
		 * Double check connection to MySQL
		 */
		try 
		{
			if(!conn.isValid(5))
			{
			reconnect();
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
			
		try 
			{
		  		PreparedStatement stmt = null;
		  		stmt = this.conn.prepareStatement(sql);
		  		stmt.executeUpdate();
		  		return true;
			} catch(SQLException ex) {
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
				return false;
			}
 	}
	
	// Get Int
	// only return first row / first field
	public Integer GetInt(String sql) {
  		PreparedStatement stmt = null;
		ResultSet rs = null;
		Integer result = 0;
		
		/*
		 * Double check connection to MySQL
		 */
		try 
		{
			if(!conn.isValid(5))
			{
			reconnect();
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		try {
			stmt = this.conn.prepareStatement(sql);
		    if (stmt.executeQuery() != null) {
		    	stmt.executeQuery();
		        rs = stmt.getResultSet();
		        if(rs.next()){
		        	result = rs.getInt(1);
		        }
		        else { result = 0; }
		    }
		} 
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}		
		
		return result;
	}
	
	// read query
	public HashMap<Integer, ArrayList<String>> Read(String sql) {
		/*
		 * Double check connection to MySQL
		 */
		try 
		{
			if(!conn.isValid(5))
			{
			reconnect();
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
  		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<Integer, ArrayList<String>> Rows = new HashMap<Integer, ArrayList<String>>();
		
		try {
			stmt = this.conn.prepareStatement(sql);
		    if (stmt.executeQuery() != null) {
		    	stmt.executeQuery();
		        rs = stmt.getResultSet();
				while (rs.next()) {
					ArrayList<String> Col = new ArrayList<String>();
					for(int i=1;i<=rs.getMetaData().getColumnCount();i++) {						
						Col.add(rs.getString(i));
					}
					Rows.put(rs.getRow(),Col);
				}
			}	    
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		// release dataset
	    if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException sqlEx) { } // ignore
	        rs = null;
	    }
	    if (stmt != null) {
	        try {
	            stmt.close();
	        } catch (SQLException sqlEx) { } // ignore
	        stmt = null;
	    }

		return Rows;
	}
	
}
