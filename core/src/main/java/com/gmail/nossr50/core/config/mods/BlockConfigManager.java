package com.gmail.nossr50.core.config.mods;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.util.ModManager;

import java.io.File;
import java.util.regex.Pattern;

public class BlockConfigManager {
    public BlockConfigManager() {
        Pattern middlePattern = Pattern.compile("blocks\\.(?:.+)\\.yml");
        Pattern startPattern = Pattern.compile("(?:.+)\\.blocks\\.yml");
        File dataFolder = new File(McmmoCore.getModDataFolderPath());
        File vanilla = new File(dataFolder, "blocks.default.yml");
        ModManager modManager = McmmoCore.getModManager();

        if (!vanilla.exists()) {
            plugin.saveResource(vanilla.getParentFile().getName() + File.separator + "blocks.default.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!middlePattern.matcher(fileName).matches() && !startPattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            modManager.registerCustomBlocks(new CustomBlockConfig(fileName));
        }
    }
}
