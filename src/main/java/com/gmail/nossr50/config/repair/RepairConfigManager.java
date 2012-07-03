package com.gmail.nossr50.config.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repairable;

public class RepairConfigManager {
    private List<Repairable> repairables;

    public RepairConfigManager(mcMMO plugin) {
        repairables = new ArrayList<Repairable>();

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
            List<Repairable> rConfigRepairables = rConfig.getLoadedRepairables();

            if (rConfigRepairables != null) {
                repairables.addAll(rConfigRepairables);
            }
        }
    }

    public List<Repairable> getLoadedRepairables() {
        if (repairables == null) {
            return new ArrayList<Repairable>();
        }

        return repairables;
    }
}
