package com.gmail.nossr50.config.skills.repair;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class RepairConfigManager {
    public static final String REPAIR_VANILLA_YML = "repair.vanilla.yml";
    private static final Collection<Repairable> repairables = new HashSet<>();

    public RepairConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("repair\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();

        RepairConfig mainRepairConfig = new RepairConfig(REPAIR_VANILLA_YML, true);
        repairables.addAll(mainRepairConfig.getLoadedRepairables());

        for (String fileName : dataFolder.list()) {
            if (fileName.equals(REPAIR_VANILLA_YML))
                continue;

            if (!pattern.matcher(fileName).matches()) {
                continue;
            }

            File file = new File(dataFolder, fileName);

            if (file.isDirectory()) {
                continue;
            }

            RepairConfig rConfig = new RepairConfig(fileName, false);
            repairables.addAll(rConfig.getLoadedRepairables());
        }
    }

    public Collection<Repairable> getLoadedRepairables() {
        return repairables;
    }
}
