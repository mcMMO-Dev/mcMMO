package com.gmail.nossr50.config.mods;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ModManager;

import java.io.File;
import java.util.regex.Pattern;

public class ToolConfigManager {
    public ToolConfigManager(mcMMO plugin) {
        Pattern middlePattern = Pattern.compile("tools\\.(?:.+)\\.yml");
        Pattern startPattern = Pattern.compile("(?:.+)\\.tools\\.yml");
        File dataFolder = new File(mcMMO.getModDirectory());
        File vanilla = new File(dataFolder, "tools.default.yml");
        ModManager modManager = mcMMO.getModManager();

        if (!vanilla.exists()) {
            plugin.saveResource(vanilla.getParentFile().getName() + File.separator + "tools.default.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!middlePattern.matcher(fileName).matches() && !startPattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            modManager.registerCustomTools(new CustomToolLegacyConfig(fileName));
        }
    }
}
