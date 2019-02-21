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
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SalvageConfig extends ConfigCollection {

    public static final String SALVAGEABLES = "Salvageables";
    public static final String MATERIAL_TYPE = "MaterialType";
    public static final String SALVAGE_MATERIAL = "SalvageMaterial";
    public static final String MAXIMUM_DURABILITY = "MaximumDurability";
    public static final String ITEM_TYPE = "ItemType";
    public static final String METADATA = "Metadata";
    public static final String MINIMUM_LEVEL = "MinimumLevel";
    public static final String XP_MULTIPLIER = "XpMultiplier";
    public static final String MAXIMUM_QUANTITY = "MaximumQuantity";

    public SalvageConfig(String fileName, boolean merge) {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), fileName, false);
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), fileName, merge);
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
            ArrayList<String> keys = new ArrayList<>(getStringValueList(SALVAGEABLES));

            for (String key : keys) {
                // Validate all the things!
                List<String> errorMessages = new ArrayList<String>();

                // ItemStack Material
                Material itemMaterial = Material.matchMaterial(key);

                if (itemMaterial == null) {
                    errorMessages.add("Salvage Config: Invalid material - " + key);
                    continue;
                }

                // Salvage Material Type
                MaterialType salvageMaterialType = MaterialType.OTHER;

                String salvageMaterialTypeString;
                
                if(hasNode(SALVAGEABLES, key, MATERIAL_TYPE))
                    salvageMaterialTypeString = getStringValue(SALVAGEABLES, key, MATERIAL_TYPE);
                else
                    salvageMaterialTypeString = "OTHER";

                if (!hasNode(SALVAGEABLES, key, MATERIAL_TYPE)) {
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
                        errorMessages.add("Salvage Config: " + key + " has an invalid MaterialType of " + salvageMaterialTypeString);
                    }
                }

                // Salvage Material
                String salvageMaterialName = getStringValue(SALVAGEABLES, key, SALVAGE_MATERIAL);
                Material salvageMaterial = (salvageMaterialName == null ? salvageMaterialType.getDefaultMaterial() : Material.matchMaterial(salvageMaterialName));

                if (salvageMaterial == null) {
                    errorMessages.add(key + " has an invalid salvage material: " + salvageMaterialName);
                    continue;
                }

                // Maximum Durability
                short maximumDurability = itemMaterial.getMaxDurability();

                // ItemStack Type
                ItemType salvageItemType = ItemType.OTHER;
                
                String salvageItemTypeString;
                
                if(hasNode(SALVAGEABLES, key, ITEM_TYPE))
                    salvageItemTypeString = getStringValue(SALVAGEABLES, key, ITEM_TYPE);
                else
                    salvageItemTypeString = "OTHER";
                
            if (!hasNode(SALVAGEABLES, key, ITEM_TYPE)) {
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
                        errorMessages.add("Salvage Config: " + key + " has an invalid " + ITEM_TYPE + " of " + salvageItemTypeString);
                    }
                }

                byte salvageMetadata = -1;
                
                if(hasNode(SALVAGEABLES, key, SALVAGE_MATERIAL, METADATA))
                    salvageMetadata = (byte) getIntValue(SALVAGEABLES, key, SALVAGE_MATERIAL, METADATA);
                
                int minimumLevel = getIntValue(SALVAGEABLES, key, MINIMUM_LEVEL);
                double xpMultiplier = 1;

                if(hasNode(SALVAGEABLES, key, XP_MULTIPLIER))
                    xpMultiplier = getDoubleValue(SALVAGEABLES, key, XP_MULTIPLIER);

                // Maximum Quantity
                int maximumQuantity = SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), salvageMaterial, salvageMetadata);

                if(hasNode(SALVAGEABLES, key, MAXIMUM_QUANTITY))
                    maximumQuantity = getIntValue(SALVAGEABLES, key, MAXIMUM_QUANTITY);


                /*
                 * VALIDATE
                 */

                if(minimumLevel < 0)
                    minimumLevel = 0;

                if(maximumQuantity < 0)
                    maximumQuantity = 1;

                if(xpMultiplier < 0)
                    xpMultiplier = 0;

                Salvageable salvageable = SalvageableFactory.getSalvageable(itemMaterial, salvageMaterial, salvageMetadata, minimumLevel, maximumQuantity, maximumDurability, salvageItemType, salvageMaterialType, xpMultiplier);
                genericCollection.add(salvageable);

                for (String issue : errorMessages) {
                    mcMMO.p.getLogger().warning(issue);
                }
            }
            
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

}
