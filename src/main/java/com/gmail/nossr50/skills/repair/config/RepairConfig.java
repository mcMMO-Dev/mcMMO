package com.gmail.nossr50.skills.repair.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.skills.repair.RepairItemType;
import com.gmail.nossr50.skills.repair.RepairMaterialType;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.skills.repair.RepairableFactory;
import com.gmail.nossr50.util.ItemUtils;

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
                plugin.getLogger().warning("You are using an old version of the " + fileName + " file.");
                plugin.getLogger().warning("You should delete your current file and allow a new one to generate.");
                plugin.getLogger().warning("Repair will not work properly until you do.");
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
            RepairMaterialType repairMaterialType = RepairMaterialType.OTHER;
            String repairMaterialTypeString = config.getString("Repairables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Repairables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.WOOD;
                }
                else if (ItemUtils.isStoneTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.STONE;
                }
                else if (ItemUtils.isStringTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.STRING;
                }
                else if (ItemUtils.isLeatherArmor(repairItem)) {
                    repairMaterialType = RepairMaterialType.LEATHER;
                }
                else if (ItemUtils.isIronArmor(repairItem) || ItemUtils.isIronTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.IRON;
                }
                else if (ItemUtils.isGoldArmor(repairItem) || ItemUtils.isGoldTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.GOLD;
                }
                else if (ItemUtils.isDiamondArmor(repairItem) || ItemUtils.isDiamondTool(repairItem)) {
                    repairMaterialType = RepairMaterialType.DIAMOND;
                }
            }
            else {
                try {
                    repairMaterialType = RepairMaterialType.valueOf(repairMaterialTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + repairMaterialTypeString);
                }
            }

            // Repair Material
            String repairMaterialName = config.getString("Repairables." + key + ".RepairMaterial");
            Material repairMaterial = (repairMaterialName == null ? repairMaterialType.getDefaultRepairMaterial() : Material.matchMaterial(repairMaterialName));

            if (repairMaterial == null) {
                reason.add(key + " has an invalid repair material: " + repairMaterialName);
            }

            // Maximum Durability
            short maximumDurability = (config.contains("Repairables." + key + ".MaximumDurability") ? (short) config.getInt("Repairables." + key + ".MaximumDurability") : (itemMaterial != null ? itemMaterial.getMaxDurability() : 0));

            if (maximumDurability <= 0) {
                reason.add("Maximum durability of " + key + " must be greater than 0!");
            }

            // Item Type
            RepairItemType repairItemType = RepairItemType.OTHER;
            String repairItemTypeString = config.getString("Repairables." + key + ".ItemType", "OTHER");

            if (!config.contains("Repairables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack repairItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(repairItem)) {
                    repairItemType = RepairItemType.TOOL;
                }
                else if (ItemUtils.isArmor(repairItem)) {
                    repairItemType = RepairItemType.ARMOR;
                }
            }
            else {
                try {
                    repairItemType = RepairItemType.valueOf(repairItemTypeString);
                }
                catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + repairItemTypeString);
                }
            }

            int repairMetadata = config.getInt("Repairables." + key + ".RepairMaterialMetadata", -1);
            int minimumLevel = config.getInt("Repairables." + key + ".MinimumLevel");
            double xpMultiplier = config.getDouble("Repairables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Minimum Quantity
            int minimumQuantity = 0;

            if (config.contains("Repairables." + key + ".MinimumQuantity")) {
                minimumQuantity = config.getInt("Repairables." + key + ".MinimumQuantity");
            }
            else if (itemMaterial != null) {
                ItemStack item = new ItemStack(itemMaterial);
                MaterialData repairData = new MaterialData(repairMaterial, (byte) repairMetadata);
                Recipe recipe = mcMMO.p.getServer().getRecipesFor(item).get(0);

                if (recipe instanceof ShapelessRecipe) {
                    for (ItemStack ingredient : ((ShapelessRecipe) recipe).getIngredientList()) {
                        if (ingredient != null && ingredient.getType() == repairMaterial && (repairMetadata == -1 || ingredient.getData() == repairData)) {
                            minimumQuantity += ingredient.getAmount();
                        }
                    }
                }
                else if (recipe instanceof ShapedRecipe) {
                    for (ItemStack ingredient : ((ShapedRecipe) recipe).getIngredientMap().values()) {
                        if (ingredient != null && ingredient.getType() == repairMaterial && (repairMetadata == -1 || ingredient.getData() == repairData)) {
                            minimumQuantity += ingredient.getAmount();
                        }
                    }
                }
            }

            if (minimumQuantity <= 0) {
                reason.add("Minimum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInRepairable(reason)) {
                Repairable repairable = RepairableFactory.getRepairable(itemMaterial, repairMaterial, (byte) repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
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
