package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public abstract class AutoUpdateConfigLoader extends ConfigLoader {
    public AutoUpdateConfigLoader(String relativePath, String fileName, File dataFolder) {
        super(relativePath, fileName, dataFolder);
    }

    public AutoUpdateConfigLoader(String fileName, File dataFolder) {
        super(fileName, dataFolder);
    }

    @Deprecated
    public AutoUpdateConfigLoader(String relativePath, String fileName) {
        super(relativePath, fileName);
    }

    @Deprecated
    public AutoUpdateConfigLoader(String fileName) {
        super(fileName);
    }

    protected void saveConfig() {
        try {
            mcMMO.p.getLogger().info("Saving changes to config file - "+fileName);
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected @NotNull FileConfiguration getInternalConfig() {
        return YamlConfiguration.loadConfiguration(mcMMO.p.getResourceAsReader(fileName));
    }

    @Override
    protected void loadFile() {
        super.loadFile();
        FileConfiguration internalConfig = YamlConfiguration.loadConfiguration(mcMMO.p.getResourceAsReader(fileName));

        try {
            internalConfig.options().parseComments(false);
            config.options().parseComments(false);
        }
        catch (NoSuchMethodError ignored) {}

        Set<String> configKeys = config.getKeys(true);
        Set<String> internalConfigKeys = internalConfig.getKeys(true);

        boolean needSave = false;

        Set<String> oldKeys = new HashSet<>(configKeys);
        oldKeys.removeAll(internalConfigKeys);

        Set<String> newKeys = new HashSet<>(internalConfigKeys);
        newKeys.removeAll(configKeys);

        // Don't need a re-save if we have old keys sticking around?
        // Would be less saving, but less... correct?
        if (!newKeys.isEmpty() || !oldKeys.isEmpty()) {
            needSave = true;
        }
//
//        for (String key : oldKeys) {
//            mcMMO.p.debug("Detected potentially unused key: " + key);
//            //config.set(key, null);
//        }

        for (String key : newKeys) {
            mcMMO.p.debug("Adding new key: " + key + " = " + internalConfig.get(key));
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

            // Read the internal config to get comments, then put them in the new one
            try {
                // Read internal
                BufferedReader reader = new BufferedReader(new InputStreamReader(mcMMO.p.getResource(fileName)));
                LinkedHashMap<String, String> comments = new LinkedHashMap<>();
                StringBuilder temp = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("#")) {
                        temp.append(line).append("\n");
                    }
                    else if (line.contains(":")) {
                        line = line.substring(0, line.indexOf(":") + 1);
                        if (temp.length() > 0) {
                            if(comments.containsKey(line)) {
                                int index = 0;
                                while(comments.containsKey(line + index)) {
                                    index++;
                                }
                                
                                line = line + index;
                            }

                            comments.put(line, temp.toString());
                            temp = new StringBuilder();
                        }
                    }
                }

                // Dump to the new one
                HashMap<String, Integer> indexed = new HashMap<>();
                for (String key : comments.keySet()) {
                    String actualkey = key.substring(0, key.indexOf(":") + 1);

                    int index = 0;
                    if(indexed.containsKey(actualkey)) {
                        index = indexed.get(actualkey);
                    }
                    boolean isAtTop = !output.contains("\n" + actualkey);
                    index = output.indexOf((isAtTop ? "" : "\n") + actualkey, index);

                    if (index >= 0) {
                        output = output.substring(0, index) + "\n" + comments.get(key) + output.substring(isAtTop ? index : index + 1);
                        indexed.put(actualkey, index + comments.get(key).length() + actualkey.length() + 1);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Save it
            if(dataFolder == null) {
                mcMMO.p.getLogger().severe("Data folder should never be null!");
                return;
            }

            try {
                String saveName = fileName;
                // At this stage we cannot guarantee that Config has been loaded, so we do the check directly here
                if (!mcMMO.p.getConfig().getBoolean("General.Config_Update_Overwrite", true)) {
                    saveName += ".new";
                }

                File newSaveFile = new File(dataFolder, saveName);
                FileWriter fileWriter = new FileWriter(newSaveFile.getAbsolutePath());
                BufferedWriter writer = new BufferedWriter(fileWriter);
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
