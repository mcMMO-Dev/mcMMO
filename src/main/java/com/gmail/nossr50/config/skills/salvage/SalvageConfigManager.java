package com.gmail.nossr50.config.skills.salvage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;

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

            SalvageConfig salvageConfig = new SalvageConfig(fileName);
            salvageables.addAll(salvageConfig.getLoadedSalvageables());
        }
    }

    public List<Salvageable> getLoadedSalvageables() {
        return salvageables;
    }
}
