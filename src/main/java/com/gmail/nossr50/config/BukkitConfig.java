package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitConfig {
    boolean copyDefaults;
    protected final String fileName;
    protected final File configFile;
    protected YamlConfiguration defaultYamlConfig;
    protected YamlConfiguration config;
    protected @NotNull
    final File dataFolder;
    private boolean savedDefaults = false;

    public BukkitConfig(@NotNull String fileName, @NotNull File dataFolder, boolean copyDefaults) {
        LogUtils.debug(mcMMO.p.getLogger(), "Initializing config: " + fileName);
        this.copyDefaults = copyDefaults;
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, fileName);
        this.defaultYamlConfig = saveDefaultConfigToDisk();
        this.config = initConfig();
        updateFile();
        LogUtils.debug(mcMMO.p.getLogger(), "Config initialized: " + fileName);
    }

    public BukkitConfig(@NotNull String fileName, @NotNull File dataFolder) {
        this(fileName, dataFolder, true);
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
            config.save(configFile);

            if (copyDefaults && !savedDefaults) {
                copyMissingDefaultsFromResource();
                savedDefaults = true;
            }
        } catch (IOException e) {
            mcMMO.p.getLogger().log(Level.SEVERE, "Unable to save config file: " + fileName, e);
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
    YamlConfiguration saveDefaultConfigToDisk() {
        LogUtils.debug(mcMMO.p.getLogger(),
                "Copying default config to disk: " + fileName + " to defaults/" + fileName);
        try (InputStream inputStream = mcMMO.p.getResource(fileName)) {
            if (inputStream == null) {
                mcMMO.p.getLogger().severe("Unable to copy default config: " + fileName);
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
            LogUtils.debug(
                    mcMMO.p.getLogger(),
                    "User config file not found, copying a default config to disk: " + fileName);
            mcMMO.p.saveResource(fileName, false);
        }

        LogUtils.debug(mcMMO.p.getLogger(), "Loading config from disk: " + fileName);
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
            LogUtils.debug(mcMMO.p.getLogger(), "No errors found in " + fileName + "!");
        } else {
            mcMMO.p.getLogger()
                    .warning("Errors were found in " + fileName + "! mcMMO was disabled!");
            mcMMO.p.getServer().getPluginManager().disablePlugin(mcMMO.p);
            mcMMO.p.noErrorsInConfigFiles = false;
        }
    }

    public void backup() {
        LogUtils.debug(mcMMO.p.getLogger(),
                "You are using an old version of the " + fileName + " file.");
        LogUtils.debug(
                mcMMO.p.getLogger(),
                "Your old file has been renamed to " + fileName
                        + ".old and has been replaced by an updated version.");

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