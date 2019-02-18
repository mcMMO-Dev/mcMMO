package com.gmail.nossr50.config.mods;


public class ToolConfigManager {
    //TODO: Commented out until modded servers appear again
    /*public ToolConfigManager(mcMMO plugin) {
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

            modManager.registerCustomTools(new CustomToolConfig(fileName));
        }
    }*/
}
