package com.gmail.nossr50.vPlayersOnline;

import java.io.File;
import java.util.HashMap;
import org.bukkit.event.player.*;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;

/**
 * vPlayersOnline for Bukkit
 *
 * @author nossr50
 */
public class vPlayersOnline extends JavaPlugin {
    private final vPlayerListener playerListener = new vPlayerListener(this);
    private final String name = "vPlayersOnline";

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        //Displays a message when plugin is loaded
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        System.out.println("vPlayersOnline disabled.");
    }
}
