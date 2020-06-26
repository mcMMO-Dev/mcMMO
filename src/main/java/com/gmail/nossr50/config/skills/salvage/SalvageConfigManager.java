package com.gmail.nossr50.config.skills.salvage;

import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.util.FixSpellingNetheriteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SalvageConfigManager {
    private final List<Salvageable> salvageables = new ArrayList<Salvageable>();

    public SalvageConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("salvage\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();
        File vanilla = new File(dataFolder, "salvage.vanilla.yml");

        if (!vanilla.exists()) {
            plugin.saveResource("salvage.vanilla.yml", false);
        }

        for (String fileName : dataFolder.list()) {
            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }


            if(mcMMO.getUpgradeManager().shouldUpgrade(UpgradeType.FIX_SPELLING_NETHERITE_SALVAGE)) {
                //Check spelling mistakes (early versions of 1.16 support had Netherite misspelled)
                plugin.getLogger().info("Checking for certain invalid material names in Salvage config...");
                FixSpellingNetheriteUtil.processFileCheck(mcMMO.p, fileName, UpgradeType.FIX_SPELLING_NETHERITE_SALVAGE);
            }


            SalvageConfig salvageConfig = new SalvageConfig(fileName);
            salvageables.addAll(salvageConfig.getLoadedSalvageables());
        }
    }

    public List<Salvageable> getLoadedSalvageables() {
        return salvageables;
    }
}
