package com.gmail.nossr50;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.*;
import com.gmail.nossr50.skills.*;
import com.gmail.nossr50.party.*;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;


public class mcMMO extends JavaPlugin {
	public static String maindirectory = "plugins/mcMMO/";
	static File Properties = new File(maindirectory + "mcmmo.properties");
    public static final Logger log = Logger.getLogger("Minecraft");
    private final mcPlayerListener playerListener = new mcPlayerListener(this);
    private final mcBlockListener blockListener = new mcBlockListener(this);
    private final mcEntityListener entityListener = new mcEntityListener(this);
    private final String name = "mcMMO";
    public static PermissionHandler PermissionsHandler = null;
    private Permissions permissions;
    private Timer mcMMO_Timer = new Timer(true);
    public static Database database = null;
    
    public void onEnable() {
    	mcMMO_Timer.schedule(new mcTimer(this), (long)0, (long)(1000));
    	new File(maindirectory).mkdir();
    	mcProperties.makeProperties(Properties, log); //Make Props file
    	LoadProperties.loadMain(); //Load Props file
    	Users.getInstance().loadUsers(); //Load Users file
    	for(Player player : getServer().getOnlinePlayers()){Users.addUser(player);} //In case of reload add all users back into PlayerProfile
        /*
         * REGISTER EVENTS
         */
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        mcPermissions.initialize(getServer());
        mcLoadMySQL(); 
        if(LoadProperties.useMySQL)
        	database.createStructure(); //Make Structure
        if(!LoadProperties.useMySQL)
        	Leaderboard.makeLeaderboards(); //Make the leaderboards
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    private void mcLoadMySQL() {
    	if (LoadProperties.useMySQL) {
    		// create database object
    		database = new Database();
    	}
    }
    
    public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if(this.PermissionsHandler == null) {
    	    if(test != null) {
    		this.PermissionsHandler = ((Permissions)test).getHandler();
    	    } else {
    		log.info(Messaging.bracketize(name) + " Permission system not enabled. Disabling plugin.");
    		this.getServer().getPluginManager().disablePlugin(this);
    	    }
    	}
    }
    public boolean isPartyChatToggled(Player player){
    	if(Config.getInstance().isPartyToggled(player.getName())){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean inSameParty(Player playera, Player playerb){
    	if(Users.getProfile(playera).inParty() && Users.getProfile(playerb).inParty()){
	        if(Users.getProfile(playera).getParty().equals(Users.getProfile(playerb).getParty())){
	            return true;
	        } else {
	            return false;
	        }
    	} else {
    		return false;
    	}
    }
    public void addXp(Player player, String skillname, Integer newvalue){
    	PlayerProfile PP = Users.getProfile(player);
    	PP.addXpToSkill(newvalue, skillname, player);
    	Skills.XpCheck(player);
    }
    public void modifySkill(Player player, String skillname, Integer newvalue){
    	PlayerProfile PP = Users.getProfile(player);
    	PP.modifyskill(newvalue, skillname);
    }
    public ArrayList<String> getParties(){
    	String location = "plugins/mcMMO/mcmmo.users";
		ArrayList<String> parties = new ArrayList<String>();
		try {
        	//Open the users file
        	FileReader file = new FileReader(location);
        	BufferedReader in = new BufferedReader(file);
        	String line = "";
        	while((line = in.readLine()) != null)
        	{
        		String[] character = line.split(":");
        		String theparty = null;
    			//Party
    			if(character.length > 3)
    				theparty = character[3];
    			if(!parties.contains(theparty))
    				parties.add(theparty);
        	}
        	in.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while reading "
            		+ location + " (Are you sure you formatted it correctly?)", e);
        }
        return parties;
	}
    public static String getPartyName(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	return PP.getParty();
    }
    public static boolean inParty(Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	return PP.inParty();
    }
    public boolean isAdminChatToggled(Player player){
    	if(Config.getInstance().isAdminToggled(player.getName())){
    		return true;
    	} else {
    		return false;
    	}
    }
    public Permissions getPermissions() {
    	return permissions;
    	}
    public void onDisable() {
        System.out.println("mcMMO was disabled.");
    }
}
