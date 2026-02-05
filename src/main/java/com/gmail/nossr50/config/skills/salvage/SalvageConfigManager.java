package com.gmail.nossr50.config.skills.salvage;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SalvageConfigManager {
    public static final String SALVAGE_VANILLA_YML = "salvage.vanilla.yml";
    private final List<Salvageable> salvageables = new ArrayList<>(); //TODO: Collision checking, make the list a set


    public SalvageConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("salvage\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();

        SalvageConfig mainSalvageConfig = new SalvageConfig(SALVAGE_VANILLA_YML);
        salvageables.addAll(mainSalvageConfig.getLoadedSalvageables());

        for (String fileName : dataFolder.list()) {
            if (fileName.equals(SALVAGE_VANILLA_YML)) {
                continue;
            }

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
        return new ArrayList<>(salvageables);
    }
}
