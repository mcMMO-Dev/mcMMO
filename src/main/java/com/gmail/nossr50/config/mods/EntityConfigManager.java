package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ModManager;

import java.io.File;
import java.util.regex.Pattern;

public class EntityConfigManager {
    public EntityConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("entities\\.(?:.+)\\.yml");
        File dataFolder = new File(mcMMO.getModDirectory());
        File vanilla = new File(dataFolder, "entities.default.yml");
        ModManager modManager = mcMMO.getModManager();

        if (!vanilla.exists()) {
            plugin.saveResource(plugin.getDataFolder().getName() + File.separator + "entities.default.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            modManager.registerCustomEntities(new CustomEntityConfig(fileName));
        }
    }
}
