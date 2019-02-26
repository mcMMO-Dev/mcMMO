package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This config
 */
public class RepairConfig extends ConfigCollection {

    public static final String REPAIRABLES = "Repairables";
    public static final String ITEM_ID = "ItemId";
    public static final String MATERIAL_TYPE = "MaterialType";
    public static final String REPAIR_MATERIAL = "RepairMaterial";
    public static final String MAXIMUM_DURABILITY = "MaximumDurability";
    public static final String ITEM_TYPE = "ItemType";
    public static final String METADATA = "Metadata";
    public static final String XP_MULTIPLIER = "XpMultiplier";
    public static final String MINIMUM_LEVEL = "MinimumLevel";
    public static final String MINIMUM_QUANTITY = "MinimumQuantity";

    public RepairConfig(String fileName, boolean merge, boolean copyDefaults) {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), fileName, false);
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), fileName, merge, copyDefaults, false);
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
        try {
            //Grab the "keys" under the Repairables node
            ArrayList<String> keys = new ArrayList<>(getStringValueList(REPAIRABLES));

            //TODO: Remove Debug
            if(keys.size() <= 0) {
                mcMMO.p.getLogger().severe("DEBUG: Repair MultiConfigContainer key list is empty");
                return;
            }

            for (String key : keys) {
                // Validate all the things!
                List<String> errorMessages = new ArrayList<String>();

                /*
                 * Match the name of the key to a Material constant definition
                 */
                Material itemMaterial = Material.matchMaterial(key);

                if (itemMaterial == null) {
                    mcMMO.p.getLogger().severe("Repair Invalid material: " + key);
                    continue;
                }

                /*
                 * Determine Repair Material Type
                 */
                MaterialType repairMaterialType = MaterialType.OTHER;
                String repairMaterialTypeString = getRepairMaterialTypeString(key);

                if (hasNode(REPAIRABLES, key, MATERIAL_TYPE)) {
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
                    //If a material cannot be matched, try matching the material to its repair material type string from the config
                    try {
                        repairMaterialType = MaterialType.valueOf(repairMaterialTypeString.toUpperCase());
                    }
                    catch (IllegalArgumentException ex) {
                        errorMessages.add("Repair Config: " + key + " has an invalid " + MATERIAL_TYPE + " of " + repairMaterialTypeString);
                        continue;
                    }
                }

                // Repair Material
                String repairMaterialName = getRepairMaterialStringName(key);
                Material repairMaterial = (repairMaterialName == null ? repairMaterialType.getDefaultMaterial() : Material.matchMaterial(repairMaterialName));

                if (repairMaterial == null) {
                    errorMessages.add(key + " has an invalid repair material: " + repairMaterialName);
                }

                // Maximum Durability
                short maximumDurability = (itemMaterial != null ? itemMaterial.getMaxDurability() : getRepairableMaximumDurability(key));

                if (maximumDurability <= 0) {
                    maximumDurability = getRepairableMaximumDurability(key);
                }

                if (maximumDurability <= 0) {
                    errorMessages.add("Maximum durability of " + key + " must be greater than 0!");
                }

                // Item Type
                ItemType repairItemType = ItemType.OTHER;
                String repairItemTypeString = "";

                if(hasNode(REPAIRABLES, key, ITEM_TYPE))
                    repairItemTypeString = getStringValue(REPAIRABLES, key, ITEM_TYPE);
                else
                    repairItemTypeString = "OTHER";

                if (!hasNode(REPAIRABLES, key, ITEM_TYPE) && itemMaterial != null) {
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
                        errorMessages.add(key + " has an invalid ItemType of " + repairItemTypeString);
                    }
                }

                byte repairMetadata = -1;

                //Set the metadata byte
                if(hasNode(REPAIRABLES, key, REPAIR_MATERIAL, METADATA))
                    repairMetadata = (byte) getIntValue(REPAIRABLES, key, REPAIR_MATERIAL, METADATA);

                int minimumLevel = getIntValue(REPAIRABLES, key, MINIMUM_LEVEL);

                double xpMultiplier = 1;

                if(hasNode(REPAIRABLES, key, XP_MULTIPLIER))
                    xpMultiplier = getDoubleValue(REPAIRABLES, key, XP_MULTIPLIER);




                // Minimum Quantity
                int minimumQuantity = SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), repairMaterial, repairMetadata);

                if (minimumQuantity <= 0) {
                    minimumQuantity = getIntValue(REPAIRABLES, key, MINIMUM_QUANTITY);
                }

                /*
                 * VALIDATE
                 * Just make sure the values we may have just grabbed from the config aren't below 0
                 */

                //Validate min level
                if(minimumLevel < 0)
                    minimumLevel = 0;

                //Validate XP Mult
                if(xpMultiplier < 0)
                    xpMultiplier = 0;

                //Validate Minimum Quantity
                if (minimumQuantity <= 0) {
                    minimumQuantity = 2;
                    errorMessages.add("Minimum quantity for "+key+" in repair config should be above 0");
                }

                Repairable repairable = RepairableFactory.getRepairable(itemMaterial, repairMaterial, repairMetadata, minimumLevel, minimumQuantity, maximumDurability, repairItemType, repairMaterialType, xpMultiplier);
                genericCollection.add(repairable);

                for (String error : errorMessages) {
                    //McmmoCore.getLogger().warning(issue);
                    mcMMO.p.getLogger().warning(error);
                }
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private String getRepairMaterialTypeString(String key) {
        return getStringValue(REPAIRABLES, key, MATERIAL_TYPE);
    }

    private short getRepairableMaximumDurability(String key) {
        return getShortValue(REPAIRABLES, key, MAXIMUM_DURABILITY);
    }

    /**
     * Gets the Repair Material String Name defined in the config
     * @param key the key name of the repairable child node under the Repairables parent node
     * @return the Repair Material String Name defined in the config
     */
    private String getRepairMaterialStringName(String key) {
        return getStringValue(REPAIRABLES, key, REPAIR_MATERIAL);
    }
}
