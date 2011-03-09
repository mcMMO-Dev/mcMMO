package com.gmail.nossr50;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	static String maindirectory = "mcMMO/";
	static File Properties = new File(maindirectory + "mcmmo.properties");
    public static final Logger log = Logger.getLogger("Minecraft");
    private final mcPlayerListener playerListener = new mcPlayerListener(this);
    private final mcBlockListener blockListener = new mcBlockListener(this);
    private final mcEntityListener entityListener = new mcEntityListener(this);
    //private final mcServerListener serverListener = new mcServerListener(this);
    private final String name = "mcMMO";
    public static PermissionHandler PermissionsHandler = null;
    private Permissions permissions;
    private Timer mcMMO_Timer = new Timer(true);
    
    //herp
    public void onEnable() {
    	//mcMMO_Timer.schedule(new mcTimer(this), 0, (long)(2000));
    	//Make the directory if it does not exist
    	new File(maindirectory).mkdir();
    	//Make the file if it does not exist
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
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.High, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
        //pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
        //Displays a message when plugin is loaded
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
