package com.bukkit.nossr50.mcMMO;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.Control;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
    public static final Logger log = Logger.getLogger("Minecraft");
    private final mcPlayerListener playerListener = new mcPlayerListener(this);
    private final mcBlockListener blockListener = new mcBlockListener(this);
    private final mcEntityListener entityListener = new mcEntityListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final String name = "mcMMO";
    public static PermissionHandler PermissionsHandler = null;
    private Permissions permissions;

    public mcMMO(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public void onEnable() {
    	mcUsers.getInstance().loadUsers();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_FLOW, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_PROJECTILE, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_BLOCK, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
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
        System.out.println("mcMMO disabled.");
    }
}
