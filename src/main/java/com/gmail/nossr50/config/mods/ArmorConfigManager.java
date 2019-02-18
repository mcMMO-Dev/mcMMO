package com.gmail.nossr50.config.mods;

public class ArmorConfigManager {
    //TODO: Commented out until modded servers appear again
    /*public ArmorConfigManager() {
        Pattern middlePattern = Pattern.compile("armor\\.(?:.+)\\.yml");
        Pattern startPattern = Pattern.compile("(?:.+)\\.armor\\.yml");
        //File dataFolder = new File(McmmoCore.getModDataFolderPath());
        File dataFolder = new File(mcMMO.p.getModDirectory());
        File vanilla = new File(dataFolder, "armor.default.yml");
        ModManager modManager = mcMMO.getModManager();

        if (!vanilla.exists()) {
            mcMMO.p.saveResource(vanilla.getParentFile().getName() + File.separator + "armor.default.yml", false);
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
    }*/
}
