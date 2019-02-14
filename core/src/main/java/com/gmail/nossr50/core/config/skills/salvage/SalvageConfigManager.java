package com.gmail.nossr50.core.config.skills.salvage;


import com.gmail.nossr50.core.skills.child.salvage.salvageables.Salvageable;

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

            com.gmail.nossr50.config.skills.salvage.SalvageConfig salvageConfig = new com.gmail.nossr50.config.skills.salvage.SalvageConfig(fileName);
            salvageables.addAll(salvageConfig.getLoadedSalvageables());
        }
    }

    public List<Salvageable> getLoadedSalvageables() {
        return salvageables;
    }
}
