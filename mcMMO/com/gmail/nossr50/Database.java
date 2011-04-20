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

import com.gmail.nossr50.config.LoadProperties;


public class Database {

	private Connection conn;
	
	public Database() {

		// Load the driver instance
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
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
	
	// write query
	public boolean Write(String sql) {
		try {
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
		
		try {
			stmt = this.conn.prepareStatement(sql);
		    if (stmt.executeQuery() != null) {
		    	stmt.executeQuery();
		        rs = stmt.getResultSet();
		        rs.next();
		        result = rs.getInt(1);
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
