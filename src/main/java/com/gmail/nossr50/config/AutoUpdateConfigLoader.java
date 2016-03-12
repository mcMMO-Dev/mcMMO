package com.gmail.nossr50.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AutoUpdateConfigLoader extends ConfigLoader {
    public AutoUpdateConfigLoader(String relativePath, String fileName) {
        super(relativePath, fileName);
    }

    public AutoUpdateConfigLoader(String fileName) {
        super(fileName);
    }

    @Override
    protected void loadFile() {
        super.loadFile();
        FileConfiguration internalConfig = YamlConfiguration.loadConfiguration(plugin.getResource(fileName));

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
            while (output.replaceAll("[//s]", "").startsWith("#")) {
                output = output.substring(output.indexOf('\n', output.indexOf('#')) + 1);
            }
            
            String[] keys = output.split("\n");
            

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
                        if (!temp.isEmpty()) {
                            comments.put(line, temp);
                            temp = "";
                        }
                    }
                }

                output = "";
                // Dump to the new one
                for (String key : keys) {
                    String comment = comments.get(key.substring(0, key.indexOf(":") + 1));
                    if (comment != null) {
                        output += comment;
                    }
                    output += key;
                    output += "\n";
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
    }
}
