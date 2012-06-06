package com.gmail.nossr50.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;

public abstract class ConfigLoader {
    protected String fileName;
    protected File configFile;
    protected File dataFolder;
    protected final mcMMO plugin;
    protected FileConfiguration config;

    public ConfigLoader(mcMMO plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName;
        dataFolder = plugin.getDataFolder();
        configFile = new File(dataFolder, File.separator + fileName);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Load this config file.
     */
    public void load() {
        if (!configFile.exists()) {
            dataFolder.mkdir();
            saveConfig();
        }

        addDefaults();
        loadKeys();
    }

    /**
     * Save this config file.
     */
    private void saveConfig() {
        try {
            config.save(configFile);
        }
        catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + configFile + ex);
        }
    }

    protected void saveIfNotExist() {
        if (!configFile.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            }
        }
        rereadFromDisk();
    }

    protected void rereadFromDisk() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Add the defaults to this config file.
     */
    protected void addDefaults() {
        config.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Load the keys from this config file.
     */
    protected abstract void loadKeys();

}
