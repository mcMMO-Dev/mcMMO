package com.gmail.nossr50.config.skills.repair;

import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.util.FixSpellingNetheriteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RepairConfigManager {
    private final List<Repairable> repairables = new ArrayList<Repairable>();

    public RepairConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("repair\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();
        File vanilla = new File(dataFolder, "repair.vanilla.yml");

        if (!vanilla.exists()) {
            plugin.saveResource("repair.vanilla.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }


            if(mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.FIX_SPELLING_NETHERITE_REPAIR)) {
                //Check spelling mistakes (early versions of 1.16 support had Netherite misspelled)
                plugin.getLogger().info("Checking for certain invalid material names in Repair config...");
                FixSpellingNetheriteUtil.processFileCheck(mcMMO.p, fileName, UpgradeType.FIX_SPELLING_NETHERITE_REPAIR);
            }

            RepairConfig rConfig = new RepairConfig(fileName);
            repairables.addAll(rConfig.getLoadedRepairables());
        }
    }

    public List<Repairable> getLoadedRepairables() {
        return repairables;
    }
}
