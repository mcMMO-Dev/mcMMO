package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.ConfigCollections;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.sk89q.worldedit.InvalidItemException;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This config
 */
public class RepairConfig extends ConfigCollections {
    private List<Repairable> repairables;

    public RepairConfig(String fileName) {
        super(McmmoCore.getDataFolderPath().getAbsoluteFile(), fileName, false);
    }

    @Override
    public void unload() {
        repairables = null;
    }

    @Override
    public Collection getLoadedCollection() {
        return repairables == null ? new ArrayList<Repairable>() : repairables;
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
    public void loadKeys() {
        repairables = new ArrayList<Repairable>();

        ConfigurationNode repairablesNode = getUserRootNode().getNode("Repairables");
        List<? extends ConfigurationNode> repairablesNodeChildrenList = repairablesNode.getChildrenList();
        Iterator<? extends ConfigurationNode> configIter = repairablesNodeChildrenList.iterator();

        for(Iterator<? extends ConfigurationNode> i = repairablesNodeChildrenList.iterator(); i.hasNext();)
        {
            ConfigurationNode iterNode = i.next();
            //TODO: Verify that this is getting the key
            String key = iterNode.getKey().toString(); //Get the String of the node

            // Validate all the things!
            List<String> reason = new ArrayList<String>();

            try {
                // ItemStack Material
                ConfigItemCategory configItemCategory = ItemUtils.matchItemType(key);
            } catch (InvalidItemException e) {
                e.printStackTrace();
            }

            if (itemType == null) {
                reason.add("Invalid material: " + key);
            }

            // Repair Material Type
            MaterialType repairMaterialType = MaterialType.OTHER;
            String repairMaterialTypeString = getStringValue("Repairables." + key + ".MaterialType", "OTHER");

            if (!config.contains("Repairables." + key + ".MaterialType") && itemType != null) {
                ItemStack repairItem = ItemStack.makeNew(itemType);

                if (ItemUtils.isWoodTool(repairItem)) {
                    repairMaterialType = MaterialType.WOOD;
                } else if (ItemUtils.isStoneTool(repairItem)) {
                    repairMaterialType = MaterialType.STONE;
                } else if (ItemUtils.isStringTool(repairItem)) {
                    repairMaterialType = MaterialType.STRING;
                } else if (ItemUtils.isLeatherArmor(repairItem)) {
                    repairMaterialType = MaterialType.LEATHER;
                } else if (ItemUtils.isIronArmor(repairItem) || ItemUtils.isIronTool(repairItem)) {
                    repairMaterialType = MaterialType.IRON;
                } else if (ItemUtils.isGoldArmor(repairItem) || ItemUtils.isGoldTool(repairItem)) {
                    repairMaterialType = MaterialType.GOLD;
                } else if (ItemUtils.isDiamondArmor(repairItem) || ItemUtils.isDiamondTool(repairItem)) {
                    repairMaterialType = MaterialType.DIAMOND;
                }
            } else {
                try {
                    repairMaterialType = MaterialType.valueOf(repairMaterialTypeString);
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid MaterialType of " + repairMaterialTypeString);
                }
            }

            // Repair Material
            String repairMaterialName = getStringValue("Repairables." + key + ".RepairMaterial");
            Material repairMaterial = (repairMaterialName == null ? repairMaterialType.getDefaultMaterial() : Material.matchMaterial(repairMaterialName));

            if (repairMaterial == null) {
                reason.add(key + " has an invalid repair material: " + repairMaterialName);
            }

            // Maximum Durability
            short maximumDurability = (itemType != null ? itemType.getMaxDurability() : (short) getIntValue("Repairables." + key + ".MaximumDurability"));

            if (maximumDurability <= 0) {
                maximumDurability = (short) getIntValue("Repairables." + key + ".MaximumDurability");
            }

            if (maximumDurability <= 0) {
                reason.add("Maximum durability of " + key + " must be greater than 0!");
            }

            // ItemStack Type
            ConfigItemCategory repairConfigItemCategory = ConfigItemCategory.OTHER;
            String repairItemTypeString = getStringValue("Repairables." + key + ".ItemType", "OTHER");

            if (!config.contains("Repairables." + key + ".ItemType") && itemType != null) {
                ItemStack repairItem = new ItemStack(itemType);

                if (ItemUtils.isMinecraftTool(repairItem)) {
                    repairConfigItemCategory = ConfigItemCategory.TOOL;
                } else if (ItemUtils.isArmor(repairItem)) {
                    repairConfigItemCategory = ConfigItemCategory.ARMOR;
                }
            } else {
                try {
                    repairConfigItemCategory = ConfigItemCategory.valueOf(repairItemTypeString);
                } catch (IllegalArgumentException ex) {
                    reason.add(key + " has an invalid ItemType of " + repairItemTypeString);
                }
            }

            byte repairMetadata = (byte) getIntValue("Repairables." + key + ".RepairMaterialMetadata", -1);
            int minimumLevel = getIntValue("Repairables." + key + ".MinimumLevel");
            double xpMultiplier = getDoubleValue("Repairables." + key + ".XpMultiplier", 1);

            if (minimumLevel < 0) {
                reason.add(key + " has an invalid MinimumLevel of " + minimumLevel);
            }

            // Minimum Quantity
            int minimumQuantity = (itemType != null ? SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemType), repairMaterial, repairMetadata) : getIntValue("Repairables." + key + ".MinimumQuantity", 2));

            if (minimumQuantity <= 0 && itemType != null) {
                minimumQuantity = getIntValue("Repairables." + key + ".MinimumQuantity", 2);
            }

            if (minimumQuantity <= 0) {
                reason.add("Minimum quantity of " + key + " must be greater than 0!");
            }

            if (noErrorsInRepairable(reason)) {
                Repairable repairable = RepairableFactory.getRepairable(itemType, repairMaterial, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairConfigItemCategory, repairMaterialType, xpMultiplier);
                repairables.add(repairable);
            }
        }
    }


    /**
     * Check if there are any errors for this repairable and if there are reports them to console
     * @param issues errors related to loading a repairable
     * @return returns true if there are no errors for this repairable
     */
    private boolean noErrorsInRepairable(List<String> issues) {
        for (String issue : issues) {
            McmmoCore.getLogger().warning(issue);
        }

        return issues.isEmpty();
    }
}
