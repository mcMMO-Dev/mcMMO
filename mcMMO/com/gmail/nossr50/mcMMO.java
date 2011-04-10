package com.gmail.nossr50;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	static String maindirectory = "plugins/mcMMO/";
	static File Properties = new File(maindirectory + "mcmmo.properties");
    public static final Logger log = Logger.getLogger("Minecraft");
    private final mcPlayerListener playerListener = new mcPlayerListener(this);
    private final mcBlockListener blockListener = new mcBlockListener(this);
    private final mcEntityListener entityListener = new mcEntityListener(this);
    private final String name = "mcMMO";
    public static PermissionHandler PermissionsHandler = null;
    private Permissions permissions;
    private Timer mcMMO_Timer = new Timer(true);
    
    public void onEnable() {
    	mcMMO_Timer.schedule(new mcTimer(this), 0, (long)(1000));
    	new File(maindirectory).mkdir();

    	if(!Properties.exists()){
	    	try {
				Properties.createNewFile();
				FileWriter writer = null;
				try {
					writer = new FileWriter(Properties);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Exception while creating " + Properties, e);
				} finally {
					try {
						if (writer != null) {
							writer.close();
						}
					} catch (IOException e) {
						log.log(Level.SEVERE, "Exception while closing writer for " + Properties, e);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	//Load the file
    	mcLoadProperties.loadMain();
    	mcUsers.getInstance().loadUsers();
    	for(Player player : getServer().getOnlinePlayers()){
         	mcUsers.addUser(player);
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_BED_ENTER, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        mcPermissions.initialize(getServer());
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
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
    	if(mcConfig.getInstance().isPartyToggled(player.getName())){
    		return true;
    	} else {
    		return false;
    	}
    }
    public boolean inSameParty(Player playera, Player playerb){
    	if(mcUsers.getProfile(playera).inParty() && mcUsers.getProfile(playerb).inParty()){
	        if(mcUsers.getProfile(playera).getParty().equals(mcUsers.getProfile(playerb).getParty())){
	            return true;
	        } else {
	            return false;
	        }
    	} else {
    		return false;
    	}
    }
    public void addXp(Player player, String skillname, Integer newvalue){
    	mcUsers.getProfile(player).addXpToSkill(newvalue, skillname);
    }
    public void modifySkill(Player player, String skillname, Integer newvalue){
    	mcUsers.getProfile(player).modifyskill(newvalue, skillname);
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
    	return mcUsers.getProfile(player).getParty();
    }
    public static boolean inParty(Player player){
    	return mcUsers.getProfile(player).inParty();
    }
    public boolean isAdminChatToggled(Player player){
    	if(mcConfig.getInstance().isAdminToggled(player.getName())){
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
