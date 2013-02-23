package com.gmail.nossr50.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.metrics.MetricsManager;

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
            plugin.getLogger().info("Creating mcMMO " + fileName + " File...");
            createFile();
        }
        else {
            plugin.getLogger().info("Loading mcMMO " + fileName + " File...");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        InputStream resource = plugin.getResource(fileName);

        if (resource == null) {
            return;
        }

        FileConfiguration internalConfig = YamlConfiguration.loadConfiguration(resource);

        Set<String> configKeys = config.getKeys(true);
        Set<String> internalConfigKeys = internalConfig.getKeys(true);

        boolean needSave = false;

        Set<String> oldKeys = new HashSet<String>(configKeys);
        oldKeys.removeAll(internalConfigKeys);

        Set<String> newKeys = new HashSet<String>(internalConfigKeys);
        newKeys.removeAll(configKeys);

        // Don't need a re-save if we have old keys sticking around?
        // Would be less saving, but less... correct?
        if (!newKeys.isEmpty() || !oldKeys.isEmpty()) {
            needSave = true;
        }

        for (String key : oldKeys) {
            plugin.debug("Removing unused key: " + key);
            config.set(key, null);
        }

        for (String key : newKeys) {
            plugin.debug("Adding new key: " + key + " = " + internalConfig.get(key));
            config.set(key, internalConfig.get(key));
        }

        if (needSave) {
            // Get Bukkit's version of an acceptable config with new keys, and no old keys
            String output = config.saveToString();

            // Convert to the superior 4 space indentation
            output = output.replace("  ", "    ");

            // Rip out Bukkit's attempt to save comments at the top of the file
            while (output.indexOf('#') != -1) {
                output = output.substring(output.indexOf('\n', output.indexOf('#'))+1);
            }

            // Read the internal config to get comments, then put them in the new one
            try {
                // Read internal
                BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResource(fileName)));
                HashMap<String, String> comments = new HashMap<String, String>();
                String temp = "";

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("#")) {
                        temp += line + "\n";
                    }
                    else if (line.contains(":")) {
                        line = line.substring(0, line.indexOf(":") + 1);
                        if(!temp.isEmpty()) {
                            comments.put(line, temp);
                            temp = "";
                        }
                    }
                }

                // Dump to the new one
                for (String key : comments.keySet()) {
                    if (output.indexOf(key) != -1) {
                        output = output.substring(0, output.indexOf(key)) + comments.get(key) + output.substring(output.indexOf(key));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Save it
            try {
                String saveName = fileName;
                // At this stage we cannot guarantee that Config has been loaded, so we do the check directly here
                if (!plugin.getConfig().getBoolean("General.Config_Update_Overwrite", true)) {
                    saveName += ".new";
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(plugin.getDataFolder(), saveName)));
                writer.write(output);
                writer.flush();
                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            for (String key : configKeys) {
                if (!config.isConfigurationSection(key) && !config.get(key).equals(internalConfig.get(key))) {
                    MetricsManager.customConfig();
                    break;
                }
            }
        }
    }

    protected abstract void loadKeys();

    protected void createFile() {
        if (configFile.exists()) {
            return;
        }

        configFile.getParentFile().mkdirs();

        InputStream inputStream = plugin.getResource(fileName);

        if (inputStream != null) {
            try {
                copyStreamToFile(inputStream, configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            plugin.getLogger().severe("Missing resource file: '" + fileName + "' please notify the plugin authors");
        }
    }

    private static void copyStreamToFile(InputStream inputStream, File file) throws Exception {
        OutputStream outputStream = new FileOutputStream(file);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        inputStream.close();
        outputStream.close();
    }
}
