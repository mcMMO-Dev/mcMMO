package com.gmail.nossr50.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;

public abstract class ConfigLoader {
    protected static final mcMMO plugin = mcMMO.p;
    protected String fileName;
    protected File configFile;
    protected FileConfiguration config;

    public ConfigLoader(String relativePath, String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), relativePath + File.separator + fileName);
        loadFile();
    }

    public ConfigLoader(String fileName) {
        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    protected void loadFile() {
        if (!configFile.exists()) {
            plugin.debug("Creating mcMMO " + fileName + " File...");
            plugin.saveResource(fileName, false);
        }
        else {
            plugin.debug("Loading mcMMO " + fileName + " File...");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    protected abstract void loadKeys();

    protected boolean validateKeys() {
        return true;
    }

    protected boolean noErrorsInConfig(List<String> issues) {
        for (String issue : issues) {
            plugin.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }

    protected void validate() {
        if (validateKeys()) {
            plugin.debug("No errors found in " + fileName + "!");
        }
        else {
            plugin.getLogger().warning("Errors were found in " + fileName + "! mcMMO was disabled!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            plugin.noErrorsInConfigFiles = false;
        }
    }

    public File getFile() {
        return configFile;
    }
}
