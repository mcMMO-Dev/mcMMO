package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class AutoUpdateLegacyConfigLoader extends LegacyConfigLoader {
    public AutoUpdateLegacyConfigLoader(String relativePath, String fileName, File dataFolder) {
        super(relativePath, fileName, dataFolder);
    }

    public AutoUpdateLegacyConfigLoader(String fileName, File dataFolder) {
        super(fileName, dataFolder);
    }

    @Deprecated
    public AutoUpdateLegacyConfigLoader(String relativePath, String fileName) {
        super(relativePath, fileName);
    }

    @Deprecated
    public AutoUpdateLegacyConfigLoader(String fileName) {
        super(fileName);
    }

    protected void saveConfig() {
        try {
            mcMMO.p.getLogger().info("Saving changes to config file - " + fileName);
            config.options().indent(2);
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

        Set<String> configKeys = config.getKeys(true);
        Set<String> internalConfigKeys = internalConfig.getKeys(true);

        boolean needSave = false;

        // keys present in current config file that are not in the template
        Set<String> oldKeys = new HashSet<>(configKeys);
        oldKeys.removeAll(internalConfigKeys);

        if (!oldKeys.isEmpty()) {
            mcMMO.p.debug("old key(s) in \"" + fileName + "\"");
            for (String key : oldKeys) {
                mcMMO.p.debug("  old-key:" + key);
            }
        }

        // keys present in template that are not in current file
        Set<String> newKeys = new HashSet<>(internalConfigKeys);
        newKeys.removeAll(configKeys);

        if (!newKeys.isEmpty()) {
            needSave = true;
        }

        for (String key : newKeys) {
            mcMMO.p.debug("Adding new key: " + key + " = " + internalConfig.get(key));
            config.set(key, internalConfig.get(key));
        }

        if (needSave) {
            // Save it

            if (dataFolder == null) {
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
                YamlConfiguration yamlConfiguration = config;
                yamlConfiguration.options().indent(4);
                yamlConfiguration.save(newSaveFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
