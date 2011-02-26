package com.gmail.nossr50;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.Control;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.player.*;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
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
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final String name = "mcMMO";
    public static PermissionHandler PermissionsHandler = null;
    private Permissions permissions;
    private Timer mcMMO_Timer = new Timer(true);

    /*
    public mcMMO(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }
    */
    //herp
    public void onEnable() {
    	mcMMO_Timer.schedule(new mcTimer(this), 0, (long)(2000));
    	//Make the directory if it does not exist
    	new File(maindirectory).mkdir();
    	//Make the file if it does not exist
    	if(!Properties.exists()){
	    	try {
				Properties.createNewFile();
				FileWriter writer = null;
				try {
					writer = new FileWriter(Properties);
					writer.append("#Turn this setting to false to disable pvp interactions completely");
					writer.append("pvp=true");
					writer.append("#Excavation Loot Toggles");
					writer.append("eggs=true");
					writer.append("apples=true");
					writer.append("cake=true");
					writer.append("music=true");
					writer.append("diamond=true");
					writer.append("glowstone=true");
					writer.append("slowsand=true");
					writer.append("netherrack=true");
					writer.append("bones=true");
					writer.append("sulphur=true");
					writer.append("coal=true");
					writer.append("mcmmo=mcmmo");
					writer.append("mcc=mcc");
					writer.append("stats=stats");
					writer.append("clay=true");
					writer.append("anvilmessages=true");
					writer.append("xpmodifier=2");
					writer.append("#Appreciate the plugin? Send me a donation via paypal nossr50@gmail.com\r\n");
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
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
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
    
    public Permissions getPermissions() {
    	return permissions;
    	}
    public void onDisable() {
        System.out.println("mcMMO was disabled.");
    }
}
