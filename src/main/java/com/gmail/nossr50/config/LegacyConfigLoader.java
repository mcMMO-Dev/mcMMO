package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@Deprecated
public abstract class LegacyConfigLoader {
    protected final File configFile;
    protected final @NotNull File dataFolder;
    protected String fileName;
    protected YamlConfiguration config;

    public LegacyConfigLoader(String relativePath, String fileName, @NotNull File dataFolder) {
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, relativePath + File.separator + fileName);
        loadFile();
    }

    public LegacyConfigLoader(String fileName, @NotNull File dataFolder) {
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, fileName);
        loadFile();
    }

    @Deprecated
    public LegacyConfigLoader(String relativePath, String fileName) {
        this.fileName = fileName;
        configFile = new File(mcMMO.p.getDataFolder(), relativePath + File.separator + fileName);
        this.dataFolder = mcMMO.p.getDataFolder();
        loadFile();
    }

    @Deprecated
    public LegacyConfigLoader(String fileName) {
        this.fileName = fileName;
        configFile = new File(mcMMO.p.getDataFolder(), fileName);
        this.dataFolder = mcMMO.p.getDataFolder();
        loadFile();
    }

    protected void loadFile() {
        if (!configFile.exists()) {
            mcMMO.p.getLogger().info("Creating mcMMO " + fileName + " File...");

            try {
                mcMMO.p.saveResource(fileName, false); // Normal files
            } catch (IllegalArgumentException ex) {
                mcMMO.p.saveResource(configFile.getParentFile().getName() + File.separator + fileName, false); // Mod files
            }
        } else {
            mcMMO.p.getLogger().info("Loading mcMMO " + fileName + " File...");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    protected abstract void loadKeys();

    protected boolean validateKeys() {
        return true;
    }

    protected boolean noErrorsInConfig(List<String> issues) {
        for (String issue : issues) {
            mcMMO.p.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }

    protected void validate() {
        if (validateKeys()) {
            mcMMO.p.debug("No errors found in " + fileName + "!");
        } else {
            mcMMO.p.getLogger().warning("Errors were found in " + fileName + "! mcMMO was disabled!");
            mcMMO.p.getServer().getPluginManager().disablePlugin(mcMMO.p);
            mcMMO.p.noErrorsInConfigFiles = false;
        }
    }

    public File getFile() {
        return configFile;
    }

    public void backup() {
        mcMMO.p.getLogger().warning("You are using an old version of the " + fileName + " file.");
        mcMMO.p.getLogger().warning("Your old file has been renamed to " + fileName + ".old and has been replaced by an updated version.");

        configFile.renameTo(new File(configFile.getPath() + ".old"));

        if (mcMMO.p.getResource(fileName) != null) {
            mcMMO.p.saveResource(fileName, true);
        }

        mcMMO.p.getLogger().warning("Reloading " + fileName + " with new values...");
        loadFile();
        loadKeys();
    }
}
