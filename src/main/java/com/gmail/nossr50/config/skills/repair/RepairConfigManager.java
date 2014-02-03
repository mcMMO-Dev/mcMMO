package com.gmail.nossr50.config.skills.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;

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

            RepairConfig rConfig = new RepairConfig(fileName);
            repairables.addAll(rConfig.getLoadedRepairables());
        }
    }

    public List<Repairable> getLoadedRepairables() {
        return repairables;
    }
}
