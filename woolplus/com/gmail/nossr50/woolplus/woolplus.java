package com.gmail.nossr50.woolplus;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wool Plus for Bukkit
 *
 * @author nossr50
 */
public class woolplus extends JavaPlugin {
    private final wPlayerListener playerListener = new wPlayerListener(this);
    private final wBlockListener blockListener = new wBlockListener(this);
    private final String name = "Wool Plus";

    public void onEnable() {
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        System.out.println("Wool Plus disabled!");
    }
}