package com.gmail.nossr50.config.skills.salvage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SalvageConfig extends ConfigLoader {
    private List<Salvageable> salvageables;

    public SalvageConfig(String fileName) {
        super(fileName);
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        salvageables = new ArrayList<Salvageable>();

        ConfigurationSection section = config.getConfigurationSection("Salvageables");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            // Item Material
            Material itemMaterial = Material.matchMaterial(key);

            if (itemMaterial == null) {
                reason.add("Invalid material: " + key);
            }

            // Salvage Material Type
            MaterialType salvageMaterialType = MaterialType.OTHER;
            String salvageMaterialTypeString = config.getString("Salvageables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Salvageables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(salvageItem)) {
                    salvageMaterialType = MaterialType.WOOD;
                }
                else if (ItemUtils.isStoneTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STONE;
                }
                else if (ItemUtils.isStringTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STRING;
                }
                else if (ItemUtils.isLeatherArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.LEATHER;
                }
                else if (ItemUtils.isIronArmor(salvageItem) || ItemUtils.isIronTool(salvageItem)) {
                    salvageMaterialType = MaterialType.IRON;
                }
                else if (ItemUtils.isGoldArmor(salvageItem) || ItemUtils.isGoldTool(salvageItem)) {
                    salvageMaterialType = MaterialType.GOLD;
                }
                else if (ItemUtils.isDiamondArmor(salvageItem) || ItemUtils.isDiamondTool(salvageItem)) {
                    salvageMaterialType = MaterialType.DIAMOND;
                }
            }
            else {
                try {
                    salvageMaterialType = MaterialType.valueOf(salvageMaterialTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + salvageMaterialTypeString);
                }
            }

            // Salvage Material
            String salvageMaterialName = config.getString("Salvageables." + key + ".SalvageMaterial");
            Material salvageMaterial = (salvageMaterialName == null ? salvageMaterialType.getDefaultMaterial() : Material.matchMaterial(salvageMaterialName));

            if (salvageMaterial == null) {
                reason.add(key + " has an invalid salvage material: " + salvageMaterialName);
            }

            // Maximum Durability
            short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : (short) config.getInt("Salvageables." + key + ".MaximumDurability"));

            // Item Type
            ItemType salvageItemType = ItemType.OTHER;
            String salvageItemTypeString = config.getString("Salvageables." + key + ".ItemType", "OTHER");

            if (!config.contains("Salvageables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(salvageItem)) {
                    salvageItemType = ItemType.TOOL;
                }
                else if (ItemUtils.isArmor(salvageItem)) {
                    salvageItemType = ItemType.ARMOR;
                }
            }
            else {
                try {
                    salvageItemType = ItemType.valueOf(salvageItemTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + salvageItemTypeString);
                }
            }

            byte salvageMetadata = (byte) config.getInt("Salvageables." + key + ".SalvageMaterialMetadata", -1);
            int minimumLevel = config.getInt("Salvageables." + key + ".MinimumLevel");
            double xpMultiplier = config.getDouble("Salvageables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Maximum Quantity
            int maximumQuantity = (itemMaterial != null ? SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), salvageMaterial, salvageMetadata) : config.getInt("Salvageables." + key + ".MaximumQuantity", 2));

            if (maximumQuantity <= 0 && itemMaterial != null) {
                maximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity", 2);
            }

            int configMaximumQuantity = config.getInt("Salvageables." + key + ".MaximumQuantity", -1);

            if (configMaximumQuantity > 0) {
                maximumQuantity = configMaximumQuantity;
            }

            if (maximumQuantity <= 0) {
                reason.add("Maximum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInSalvageable(reason)) {
                Salvageable salvageable = SalvageableFactory.getSalvageable(itemMaterial, salvageMaterial, salvageMetadata, minimumLevel, maximumQuantity, maximumDurability, salvageItemType, salvageMaterialType, xpMultiplier);
                salvageables.add(salvageable);
            }
        }
    }

    protected List<Salvageable> getLoadedSalvageables() {
        return salvageables == null ? new ArrayList<Salvageable>() : salvageables;
    }

    private boolean noErrorsInSalvageable(List<String> issues) {
        if (!issues.isEmpty()) {
            plugin.getLogger().warning("Errors have been found in: " + fileName);
            plugin.getLogger().warning("The following issues were found:");
        }

        for (String issue : issues) {
            plugin.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
