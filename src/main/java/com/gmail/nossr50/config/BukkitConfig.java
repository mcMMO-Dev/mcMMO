package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BukkitConfig {
    public static final String CONFIG_PATCH_PREFIX = "ConfigPatchVersion:";
    public static final String CURRENT_CONFIG_PATCH_VER = "ConfigPatchVersion: 2";
    public static final char COMMENT_PREFIX = '#';
    protected final String fileName;
    protected final File configFile;
    protected YamlConfiguration config;
    protected @NotNull
    final File dataFolder;

    public BukkitConfig(@NotNull String fileName, @NotNull File dataFolder) {
        mcMMO.p.getLogger().info("[config] Initializing config: " + fileName);
        this.fileName = fileName;
        this.dataFolder = dataFolder;
        configFile = new File(dataFolder, fileName);
        // purgeComments(true);
        this.config = initConfig();
        initDefaults();
        updateFile();
        mcMMO.p.getLogger().info("[config] Config initialized: " + fileName);
    }

    @Deprecated
    public BukkitConfig(@NotNull String fileName) {
        this(fileName, mcMMO.p.getDataFolder());
    }

    /**
     * Initialize default values for the config
     */
    public void initDefaults() {}

    /**
     * Update the file on the disk by copying out any new and missing defaults
     */
    public void updateFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private YamlConfiguration initConfig() {
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
        mcMMO.p.getLogger().severe("You are using an old version of the " + fileName + " file.");
        mcMMO.p.getLogger().severe("Your old file has been renamed to " + fileName + ".old and has been replaced by an updated version.");

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

//    /**
//     * Somewhere between December 2021-January 2022 Spigot updated their
//     * SnakeYAML dependency/API and due to our own crappy legacy code
//     * this introduced a very problematic bug where comments got duplicated
//     * <p>
//     * This method hotfixes the problem by just deleting any existing comments
//     * it's ugly, but it gets the job done
//     *
//     * @param silentFail when true mcMMO will report errors during the patch process or debug information
//     *                   the option to have it fail silently is because mcMMO wants to check files before they are parsed as a file with a zillion comments will fail to even load
//     */
//    private void purgeComments(boolean silentFail) {
//        if(!configFile.exists())
//            return;
//
//        int dupedLines = 0, lineCount = 0, lineCountAfter = 0;
//        try (FileReader fileReader = new FileReader(configFile);
//             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            Set<String> seenBefore = new HashSet<>();
//
//            stringBuilder.append(CURRENT_CONFIG_PATCH_VER).append(System.lineSeparator());
//            boolean noPatchNeeded = false;
//
//            // While not at the end of the file
//            while ((line = bufferedReader.readLine()) != null) {
//                lineCount++;
//
//                if(line.startsWith(CURRENT_CONFIG_PATCH_VER)) {
//                    noPatchNeeded = true;
//                    break;
//                }
//
//                //Older version, don't append this line
//                if(line.startsWith(CONFIG_PATCH_PREFIX))
//                    continue;
//
//                if (isFirstCharAsciiCharacter(line, COMMENT_PREFIX)) {
//                    if(seenBefore.contains(line))
//                        dupedLines++;
//                    else
//                        seenBefore.add(line);
//
//                    continue; //Delete the line by not appending it
//                }
//
//                stringBuilder
//                        .append(line) //Convert existing files into two-spaced format
//                        .append(System.lineSeparator());
//                lineCountAfter++;
//            }
//
//            if(noPatchNeeded)
//                return;
//
//            if(lineCount == 0 && !silentFail) {
//                mcMMO.p.getLogger().info("[config patcher] Config line count: " + lineCount);
//                throw new InvalidConfigurationException("[config patcher] Patching of config file resulted in an empty file, this will not be saved. Contact the mcMMO devs!");
//            }
//
//            if(dupedLines > 0 && !silentFail) {
//                mcMMO.p.getLogger().info("[config patcher] Found "+dupedLines+" duplicate comments in config file: " + configFile.getName());
//                mcMMO.p.getLogger().info("[config patcher] Purging the duplicate comments... (Nothing is broken, this is just info used for debugging)");
//                mcMMO.p.getLogger().info("[config patcher] Line count before: "+lineCount);
//                mcMMO.p.getLogger().info("[config patcher] Line count after: "+lineCountAfter);
//            }
//
//            // Write out the *patched* file
//            // AKA the file without any comments
//            try (FileWriter fileWriter = new FileWriter(configFile)) {
//                fileWriter.write(stringBuilder.toString());
//            }
//        } catch (IOException | InvalidConfigurationException ex) {
//            mcMMO.p.getLogger().severe("Failed to patch config file: " + configFile.getName());
//            ex.printStackTrace();
//        }
//    }

    private boolean isFirstCharAsciiCharacter(String line, char character) {
        if(line == null || line.isEmpty()) {
            return true;
        }

        for(Character c : line.toCharArray()) {
            if(c.equals(' '))
                continue;

            return c.equals(character);
        }

        return false;
    }
}