package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class BukkitConfig {
    boolean copyDefaults = true;
    protected final String fileName;
    protected final File configFile;
    protected YamlConfiguration defaultYamlConfig;
    protected YamlConfiguration config;
    protected @NotNull final File dataFolder;

    public BukkitConfig(@NotNull String fileName, @NotNull File dataFolder) {
        mcMMO.p.getLogger().info("[config] Initializing config: " + fileName);
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, fileName);
        this.defaultYamlConfig = copyDefaultConfig();
        this.config = initConfig();
        updateFile();
        mcMMO.p.getLogger().info("[config] Config initialized: " + fileName);
    }

    public BukkitConfig(@NotNull String fileName, @NotNull File dataFolder, boolean copyDefaults) {
        mcMMO.p.getLogger().info("[config] Initializing config: " + fileName);
        this.copyDefaults = copyDefaults;
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, fileName);
        this.defaultYamlConfig = copyDefaultConfig();
        this.config = initConfig();
        updateFile();
        mcMMO.p.getLogger().info("[config] Config initialized: " + fileName);
    }

    public BukkitConfig(@NotNull String fileName) {
        this(fileName, mcMMO.p.getDataFolder());
    }
    public BukkitConfig(@NotNull String fileName, boolean copyDefaults) {
        this(fileName, mcMMO.p.getDataFolder(), copyDefaults);
    }

    /**
     * Update the file on the disk by copying out any new and missing defaults
     */
    public void updateFile() {
        try {
            if(copyDefaults) {
                copyMissingDefaultsFromResource();
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies missing keys and values from the internal resource config within the JAR
     */
    private void copyMissingDefaultsFromResource() {
        boolean updated = false;
        for (String key : defaultYamlConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultYamlConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            updateFile();
        }
    }

    /**
     * Copies the config from the JAR to defaults/<fileName>
     */
    YamlConfiguration copyDefaultConfig() {
        mcMMO.p.getLogger().info("[config] Copying default config to disk: " + fileName + " to defaults/" + fileName);
        try(InputStream inputStream = mcMMO.p.getResource(fileName)) {
            if(inputStream == null) {
                mcMMO.p.getLogger().severe("[config] Unable to copy default config: " + fileName);
                return null;
            }

            //Save default file into defaults/<fileName>
            File defaultsFolder = new File(dataFolder, "defaults");
            if (!defaultsFolder.exists()) {
                defaultsFolder.mkdir();
            }
            File defaultFile = new File(defaultsFolder, fileName);
            Path path = defaultFile.toPath();
            Files.copy(inputStream, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Load file into YAML config
            YamlConfiguration defaultYamlConfig = new YamlConfiguration();
            defaultYamlConfig.load(defaultFile);
            return defaultYamlConfig;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    YamlConfiguration initConfig() {
        if (!configFile.exists()) {
            mcMMO.p.getLogger().info("[config] User config file not found, copying a default config to disk: " + fileName);
            mcMMO.p.saveResource(fileName, false);
        }

        mcMMO.p.getLogger().info("[config] Loading config from disk: " + fileName);
        YamlConfiguration config = new YamlConfiguration();
        config.options().indent(4);

        try {
            config.options().parseComments(true);
        } catch (NoSuchMethodError e) {
            //e.printStackTrace();
            // mcMMO.p.getLogger().severe("Your Spigot/CraftBukkit API is out of date, update your server software!");
        }

        config.options().copyDefaults(true);

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return config;
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

    public void backup() {
        mcMMO.p.getLogger().info("You are using an old version of the " + fileName + " file.");
        mcMMO.p.getLogger().info("Your old file has been renamed to " + fileName + ".old and has been replaced by an updated version.");

        configFile.renameTo(new File(configFile.getPath() + ".old"));

        if (mcMMO.p.getResource(fileName) != null) {
            mcMMO.p.saveResource(fileName, true);
        }

        mcMMO.p.getLogger().warning("Reloading " + fileName + " with new values...");
        initConfig();
        loadKeys();
    }

    public File getFile() {
        return configFile;
    }
}