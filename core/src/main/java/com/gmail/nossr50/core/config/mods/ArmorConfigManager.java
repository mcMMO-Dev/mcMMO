package com.gmail.nossr50.core.config.mods;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.util.ModManager;

import java.io.File;
import java.util.regex.Pattern;

public class ArmorConfigManager {
    public ArmorConfigManager() {
        Pattern middlePattern = Pattern.compile("armor\\.(?:.+)\\.yml");
        Pattern startPattern = Pattern.compile("(?:.+)\\.armor\\.yml");
        File dataFolder = new File(McmmoCore.getModDataFolderPath());
        File vanilla = new File(dataFolder, "armor.default.yml");
        ModManager modManager = mcMMO.getModManager();

        if (!vanilla.exists()) {
            plugin.saveResource(vanilla.getParentFile().getName() + File.separator + "armor.default.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!middlePattern.matcher(fileName).matches() && !startPattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            modManager.registerCustomArmor(new CustomArmorConfig(fileName));
        }
    }
}
