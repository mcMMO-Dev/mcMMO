package com.gmail.nossr50.runnables;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.FileManager;

import com.gmail.nossr50.McMMO;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.spout.SpoutStuff;

public class SpoutStart implements Runnable{
    private final McMMO plugin;

    public SpoutStart(final McMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getServer().getPluginManager().getPlugin("Spout") != null) {
            McMMO.spoutEnabled = true;
        }
        else {
            McMMO.spoutEnabled = false;
        }

        //Spout Stuff
        if (McMMO.spoutEnabled) {
            SpoutConfig.getInstance();
            SpoutStuff.setupSpoutConfigs();
            SpoutStuff.registerCustomEvent();
            SpoutStuff.extractFiles(); //Extract source materials

            FileManager FM = SpoutManager.getFileManager();
            FM.addToPreLoginCache(plugin, SpoutStuff.getFiles());
        }
    }
}
