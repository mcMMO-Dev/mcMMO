package com.gmail.nossr50.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.Repairable;

public class RepairConfigManager {
    private List<Repairable> repairables;

    public RepairConfigManager(mcMMO plugin) {
        Pattern pattern = Pattern.compile("repair\\.(?:.+)\\.yml");
        File dataFolder = plugin.getDataFolder();
        for(String location : dataFolder.list()) {
            if(!pattern.matcher(location).matches()) continue;

            File file = new File(dataFolder, location);
            if(file.isDirectory()) continue;

            RepairConfig rConfig = new RepairConfig(plugin, location);
            rConfig.load();

            List<Repairable> rConfigRepairables = rConfig.getLoadedRepairables();
            if(rConfigRepairables != null) {
                repairables.addAll(rConfigRepairables);
            }
        }
    }

    public List<Repairable> getLoadedRepairables() {
        if(repairables == null) return new ArrayList<Repairable>();
        return repairables;
    }
}
