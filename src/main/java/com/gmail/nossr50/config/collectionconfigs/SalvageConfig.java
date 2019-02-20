package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SalvageConfig extends ConfigCollection {

    public SalvageConfig(String fileName) {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), fileName, false);
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), fileName, false);
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public void register() {
        CommentedConfigurationNode section = getUserRootNode().getNode("Salvageables");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            // ItemStack Material
            Material itemMaterial = Material.matchMaterial(key);

            if (itemMaterial == null) {
                reason.add("Invalid material: " + key);
            }

            // Salvage Material Type
            MaterialType salvageMaterialType = MaterialType.OTHER;
            String salvageMaterialTypeString = getStringValue("Salvageables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Salvageables." + key + ".MaterialType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isWoodTool(salvageItem)) {
                    salvageMaterialType = MaterialType.WOOD;
                } else if (ItemUtils.isStoneTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STONE;
                } else if (ItemUtils.isStringTool(salvageItem)) {
                    salvageMaterialType = MaterialType.STRING;
                } else if (ItemUtils.isLeatherArmor(salvageItem)) {
                    salvageMaterialType = MaterialType.LEATHER;
                } else if (ItemUtils.isIronArmor(salvageItem) || ItemUtils.isIronTool(salvageItem)) {
                    salvageMaterialType = MaterialType.IRON;
                } else if (ItemUtils.isGoldArmor(salvageItem) || ItemUtils.isGoldTool(salvageItem)) {
                    salvageMaterialType = MaterialType.GOLD;
                } else if (ItemUtils.isDiamondArmor(salvageItem) || ItemUtils.isDiamondTool(salvageItem)) {
                    salvageMaterialType = MaterialType.DIAMOND;
                }
            } else {
                try {
                    salvageMaterialType = MaterialType.valueOf(salvageMaterialTypeString.replace(" ", "_").toUpperCase());
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + salvageMaterialTypeString);
                }
            }

            // Salvage Material
            String salvageMaterialName = getStringValue("Salvageables." + key + ".SalvageMaterial");
            Material salvageMaterial = (salvageMaterialName == null ? salvageMaterialType.getDefaultMaterial() : Material.matchMaterial(salvageMaterialName));

            if (salvageMaterial == null) {
                reason.add(key + " has an invalid salvage material: " + salvageMaterialName);
            }

            // Maximum Durability
            short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : (short) getIntValue("Salvageables." + key + ".MaximumDurability"));

            // ItemStack Type
            ItemType salvageItemType = ItemType.OTHER;
            String salvageItemTypeString = getStringValue("Salvageables." + key + ".ItemType", "OTHER");

            if (!config.contains("Salvageables." + key + ".ItemType") && itemMaterial != null) {
                ItemStack salvageItem = new ItemStack(itemMaterial);

                if (ItemUtils.isMinecraftTool(salvageItem)) {
                    salvageItemType = ItemType.TOOL;
                } else if (ItemUtils.isArmor(salvageItem)) {
                    salvageItemType = ItemType.ARMOR;
                }
            } else {
                try {
                    salvageItemType = ItemType.valueOf(salvageItemTypeString.replace(" ", "_").toUpperCase());
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + salvageItemTypeString);
                }
            }

            byte salvageMetadata = (byte) getIntValue("Salvageables." + key + ".SalvageMaterialMetadata", -1);
            int minimumLevel = getIntValue("Salvageables." + key + ".MinimumLevel");
            double xpMultiplier = getDoubleValue("Salvageables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Maximum Quantity
            int maximumQuantity = (itemMaterial != null ? SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), salvageMaterial, salvageMetadata) : getIntValue("Salvageables." + key + ".MaximumQuantity", 2));

            if (maximumQuantity <= 0 && itemMaterial != null) {
                maximumQuantity = getIntValue("Salvageables." + key + ".MaximumQuantity", 1);
            }

            int configMaximumQuantity = getIntValue("Salvageables." + key + ".MaximumQuantity", -1);

            if (configMaximumQuantity > 0) {
                maximumQuantity = configMaximumQuantity;
            }

            if (maximumQuantity <= 0) {
                reason.add("Maximum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInSalvageable(reason)) {
                Salvageable salvageable = SalvageableFactory.getSalvageable(itemMaterial, salvageMaterial, salvageMetadata, minimumLevel, maximumQuantity, maximumDurability, salvageItemType, salvageMaterialType, xpMultiplier);
                genericCollection.add(salvageable);
            }
        }
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
