package com.gmail.nossr50.config.repair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.skills.repair.RepairItemType;
import com.gmail.nossr50.skills.repair.RepairMaterialType;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableFactory;

public class RepairConfig extends ConfigLoader {
    private List<Repairable> repairables;

    public RepairConfig(String fileName) {
        super(fileName);
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        repairables = new ArrayList<Repairable>();

        ConfigurationSection section = config.getConfigurationSection("Repairables");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            if (!config.contains("Repairables." + key + ".ItemId")) {
                reason.add(key + " is missing ItemId");
            }

            if (!config.contains("Repairables." + key + ".RepairMaterialId")) {
                reason.add(key + " is missing RepairMaterialId");
            }

            if (!config.contains("Repairables." + key + ".MaximumDurability")) {
                reason.add(key + " is missing MaximumDurability");
            }

            int itemId = config.getInt("Repairables." + key + ".ItemId", 0);
            int repairMaterialId = config.getInt("Repairables." + key + ".RepairMaterialId", 0);
            int maximumDurability = config.getInt("Repairables." + key + ".MaximumDurability", 0);

            int repairMetadata = config.getInt("Repairables." + key + ".RepairMaterialMetadata", -1);
            int minimumLevel = config.getInt("Repairables." + key + ".MinimumLevel", 0);
            int minimumQuantity = config.getInt("Repairables." + key + ".MinimumQuantity", 2);
            double xpMultiplier = config.getDouble("Repairables." + key + ".XpMultiplier", 1);

            RepairItemType repairItemType = RepairItemType.OTHER;
            RepairMaterialType repairMaterialType = RepairMaterialType.OTHER;

            String repairItemTypeString = config.getString("Repairables." + key + ".ItemType", "OTHER");
            String repairMaterialTypeString = config.getString("Repairables." + key + ".MaterialType", "OTHER");

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            if (minimumQuantity < 0) {
                reason.add(key + " has an invalid MinimumQuantity of " + minimumQuantity);
            }

            try {
                repairItemType = RepairItemType.valueOf(repairItemTypeString);
            }
            catch (IllegalArgumentException ex) {
                reason.add(key + " has an invalid ItemType of " + repairItemTypeString);
            }

            try {
                repairMaterialType = RepairMaterialType.valueOf(repairMaterialTypeString);
            }
            catch (IllegalArgumentException ex) {
                reason.add(key + " has an invalid MaterialType of " + repairMaterialTypeString);
            }

            if (noErrorsInRepairable(reason)) {
                Repairable repairable = RepairableFactory.getRepairable(itemId, repairMaterialId, (byte) repairMetadata, minimumLevel, minimumQuantity, (short) maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
                repairables.add(repairable);
            }
        }
    }

    protected List<Repairable> getLoadedRepairables() {
        if (repairables == null) {
            return new ArrayList<Repairable>();
        }

        return repairables;
    }

    private boolean noErrorsInRepairable(List<String> issues) {
        if (issues.isEmpty()) {
            return true;
        }
        else {
            for (String issue : issues) {
                plugin.getLogger().warning(issue);
            }
            return false;
        }
    }
}
