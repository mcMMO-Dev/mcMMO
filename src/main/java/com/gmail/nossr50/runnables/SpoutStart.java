package com.gmail.nossr50.runnables;

import org.bukkit.Bukkit;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.FileManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;

public class SpoutStart implements Runnable{
    
    mcMMO plugin;
    
    public SpoutStart(mcMMO m) {
        plugin = m;
    }
    
    @Override
    public void run() {
        if (Bukkit.getPluginManager().getPlugin("Spout") != null) {
            LoadProperties.spoutEnabled = true;
        }
        else {
            LoadProperties.spoutEnabled = false;
        }
        
        //Spout Stuff
        if (LoadProperties.spoutEnabled) {
            SpoutStuff.setupSpoutConfigs();
            SpoutStuff.registerCustomEvent();
            SpoutStuff.extractFiles(); //Extract source materials

            FileManager FM = SpoutManager.getFileManager();
            FM.addToPreLoginCache(plugin, SpoutStuff.getFiles());
        }
    }
}
