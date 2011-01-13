package vMinecraft;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
public class vMinecraft extends JavaPlugin {

private vPlayerListener playerListener;
private vBlockListener blockListener;
public static Logger log;
public final static String name = "vMinecraft";
public final static String version = "1.0";

//We need this public/super stuff, not sure why
public vMinecraft(PluginLoader pluginLoader, Server instance, File plugin, PluginDescriptionFile desc, ClassLoader cLoader) {
super(pluginLoader, instance, desc, plugin, cLoader);
// TODO Auto-generated constructor stub
}
private vMinecraft plugin;

    public void onEnable() {
        //Register events
        registerEvents();

        // Register our events
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
    
    private void registerEvents() {
        //These are the events, as far as I know they work a lot like hooks from hMod... if not exactly
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
    }
}