package com.gmail.nossr50.config.skills.repair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

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
            if (config.contains("Repairables." + key + ".ItemId")) {
                backup();
                return;
            }

            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            // Item Material
            Material itemMaterial = Material.matchMaterial(key);

            if (itemMaterial == null) {
                reason.add("Invalid material: " + key);
            }

            // Repair Material Type
            MaterialType repairMaterialType = MaterialType.OTHER;
            String repairMaterialTypeString = config.getString("Repairables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Repairables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(repairItem)) {
                    repairMaterialType = MaterialType.WOOD;
                }
                else if (ItemUtils.isStoneTool(repairItem)) {
                    repairMaterialType = MaterialType.STONE;
                }
                else if (ItemUtils.isStringTool(repairItem)) {
                    repairMaterialType = MaterialType.STRING;
                }
                else if (ItemUtils.isLeatherArmor(repairItem)) {
                    repairMaterialType = MaterialType.LEATHER;
                }
                else if (ItemUtils.isIronArmor(repairItem) || ItemUtils.isIronTool(repairItem)) {
                    repairMaterialType = MaterialType.IRON;
                }
                else if (ItemUtils.isGoldArmor(repairItem) || ItemUtils.isGoldTool(repairItem)) {
                    repairMaterialType = MaterialType.GOLD;
                }
                else if (ItemUtils.isDiamondArmor(repairItem) || ItemUtils.isDiamondTool(repairItem)) {
                    repairMaterialType = MaterialType.DIAMOND;
                }
            }
            else {
                try {
                    repairMaterialType = MaterialType.valueOf(repairMaterialTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + repairMaterialTypeString);
                }
            }

            // Repair Material
            String repairMaterialName = config.getString("Repairables." + key + ".RepairMaterial");
            Material repairMaterial = (repairMaterialName == null ? repairMaterialType.getDefaultMaterial() : Material.matchMaterial(repairMaterialName));

            if (repairMaterial == null) {
                reason.add(key + " has an invalid repair material: " + repairMaterialName);
            }

            // Maximum Durability
            short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : (short) config.getInt("Repairables." + key + ".MaximumDurability"));

            if (maximumDurability <= 0) {
                maximumDurability = (short) config.getInt("Repairables." + key + ".MaximumDurability");
            }

            if (maximumDurability <= 0) {
                reason.add("Maximum durability of " + key + " must be greater than 0!");
            }

            // Item Type
            ItemType repairItemType = ItemType.OTHER;
            String repairItemTypeString = config.getString("Repairables." + key + ".ItemType", "OTHER");

            if (!config.contains("Repairables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(repairItem)) {
                    repairItemType = ItemType.TOOL;
                }
                else if (ItemUtils.isArmor(repairItem)) {
                    repairItemType = ItemType.ARMOR;
                }
            }
            else {
                try {
                    repairItemType = ItemType.valueOf(repairItemTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + repairItemTypeString);
                }
            }

            byte repairMetadata = (byte) config.getInt("Repairables." + key + ".RepairMaterialMetadata", -1);
            int minimumLevel = config.getInt("Repairables." + key + ".MinimumLevel");
            double xpMultiplier = config.getDouble("Repairables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Minimum Quantity
            int minimumQuantity = (itemMaterial != null ? SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), repairMaterial, repairMetadata) : config.getInt("Repairables." + key + ".MinimumQuantity", 2));

            if (minimumQuantity <= 0 && itemMaterial != null) {
                minimumQuantity = config.getInt("Repairables." + key + ".MinimumQuantity", 2);
            }

            if (minimumQuantity <= 0) {
                reason.add("Minimum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInRepairable(reason)) {
                Repairable repairable = RepairableFactory.getRepairable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
                repairables.add(repairable);
            }
        }
    }

    protected List<Repairable> getLoadedRepairables() {
        return repairables == null ? new ArrayList<Repairable>() : repairables;
    }

    private boolean noErrorsInRepairable(List<String> issues) {
        for (String issue : issues) {
            plugin.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
