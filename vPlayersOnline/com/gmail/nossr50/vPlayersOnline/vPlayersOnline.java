package com.gmail.nossr50.vPlayersOnline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * vPlayersOnline for Bukkit
 *
 * @author nossr50
 */
public class vPlayersOnline extends JavaPlugin {
    private PluginDescriptionFile pdfFile;

    private vPlayerListener playerListener;

    public void onLoad() {

    }

    public void onEnable() {
        pdfFile = this.getDescription();
        Config.name = pdfFile.getName();

        Properties config = Config.loadConfig();
        playerListener = new vPlayerListener(this, config);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);

        //Displays a message when plugin is loaded
        System.out.println(Config.name + " version " + pdfFile.getVersion() + " is enabled!");
    }
    public void onDisable() {
        System.out.println(Config.name + " disabled.");
    }
}
