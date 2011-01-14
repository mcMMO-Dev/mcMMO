package com.bukkit.nossr50.vPlayersOnline;
import java.io.File;
import java.util.HashMap;
import org.bukkit.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * PlayersOnline for Bukkit
 *
 * @author nossr50
 */
public class vPlayersOnline extends JavaPlugin {
    private final POPlayerListener playerListener = new POPlayerListener(this);
    private final POBlockListener blockListener = new POBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public final static String name = "vPlayersOnline";
    public final static String version = "1.0";
	

    public vPlayersOnline(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
    	super(pluginLoader, instance, desc, plugin, cLoader);
        
        // TODO: Place any custom initialization code here

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

   

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        // Register our events
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
    	PluginManager pm = getServer().getPluginManager();
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

