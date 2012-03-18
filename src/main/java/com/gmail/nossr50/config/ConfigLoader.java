package com.gmail.nossr50.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.nossr50.mcMMO;

public abstract class ConfigLoader {

    protected static File configFile;
    protected static File dataFolder;
    protected final mcMMO plugin;
    protected static FileConfiguration config;

    public ConfigLoader(mcMMO plugin, String fileName){
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder();
        configFile = new File(dataFolder, File.separator + fileName);
    }

    /**
     * Load this config file.
     */
    protected abstract void load();

    /**
     * Save this config file.
     */
    private static void saveConfig() {
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the defaults to this config file.
     */
    protected void addDefaults() {

        // Load from included config.yml
        config.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Load the keys from this config file.
     */
    protected abstract void loadKeys();

}
