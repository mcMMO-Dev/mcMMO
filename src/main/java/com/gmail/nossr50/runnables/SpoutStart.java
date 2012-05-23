package com.gmail.nossr50.runnables;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.FileManager;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.spout.SpoutStuff;

public class SpoutStart implements Runnable{
    private final mcMMO plugin;

    public SpoutStart(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getServer().getPluginManager().getPlugin("Spout") != null) {
            plugin.spoutEnabled = true;
        }
        else {
            plugin.spoutEnabled = false;
        }

        //Spout Stuff
        if (plugin.spoutEnabled) {
            SpoutConfig.getInstance().load();
            SpoutStuff.setupSpoutConfigs();
            SpoutStuff.registerCustomEvent();
            SpoutStuff.extractFiles(); //Extract source materials

            FileManager FM = SpoutManager.getFileManager();
            FM.addToPreLoginCache(plugin, SpoutStuff.getFiles());
        }
    }
}
