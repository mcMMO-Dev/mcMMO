package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.ConfigCollection;
import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.ItemType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableFactory;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
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

    public SalvageConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), fileName, false);
        super("salvage", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_SKILLS_DIR, true, false, true, false);
        register();
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
        //Grab the "keys" under the Repairables node
        ArrayList<ConfigurationNode> salvageChildrenNodes = new ArrayList<>(getChildren(SALVAGEABLES));

        for (ConfigurationNode salvageChildNode : salvageChildrenNodes) {
            // Validate all the things!
            List<String> errorMessages = new ArrayList<String>();

            // ItemStack Material
            String salvageChildNodeName = salvageChildNode.getString();
            Material itemMaterial = Material.matchMaterial(salvageChildNodeName);

            if (itemMaterial == null) {
                errorMessages.add("Salvage Config: Invalid material - " + salvageChildNodeName);
                continue;
            }

            // Salvage Material Type
            MaterialType salvageMaterialType = MaterialType.OTHER;

            String salvageMaterialTypeString;

            if(hasNode(SALVAGEABLES, salvageChildNodeName, MATERIAL_TYPE))
                salvageMaterialTypeString = getStringValue(SALVAGEABLES, salvageChildNodeName, MATERIAL_TYPE);
            else
                salvageMaterialTypeString = "OTHER";

            if (!hasNode(SALVAGEABLES, salvageChildNodeName, MATERIAL_TYPE)) {
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
                    errorMessages.add("Salvage Config: " + salvageChildNodeName + " has an invalid MaterialType of " + salvageMaterialTypeString);
                }
            }

            // Salvage Material
            String salvageMaterialName = getStringValue(SALVAGEABLES, salvageChildNodeName, SALVAGE_MATERIAL);
            Material salvageMaterial = (salvageMaterialName == null ? salvageMaterialType.getDefaultMaterial() : Material.matchMaterial(salvageMaterialName));

            if (salvageMaterial == null) {
                errorMessages.add(salvageChildNodeName + " has an invalid salvage material: " + salvageMaterialName);
                continue;
            }

            // Maximum Durability
            short maximumDurability = itemMaterial.getMaxDurability();

            // ItemStack Type
            ItemType salvageItemType = ItemType.OTHER;

            String salvageItemTypeString;

            if(hasNode(SALVAGEABLES, salvageChildNodeName, ITEM_TYPE))
                salvageItemTypeString = getStringValue(SALVAGEABLES, salvageChildNodeName, ITEM_TYPE);
            else
                salvageItemTypeString = "OTHER";

        if (!hasNode(SALVAGEABLES, salvageChildNodeName, ITEM_TYPE)) {
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
                    errorMessages.add("Salvage Config: " + salvageChildNodeName + " has an invalid " + ITEM_TYPE + " of " + salvageItemTypeString);
                }
            }

            byte salvageMetadata = -1;

            if(hasNode(SALVAGEABLES, salvageChildNodeName, SALVAGE_MATERIAL, METADATA))
                salvageMetadata = (byte) getIntValue(SALVAGEABLES, salvageChildNodeName, SALVAGE_MATERIAL, METADATA);

            int minimumLevel = getIntValue(SALVAGEABLES, salvageChildNodeName, MINIMUM_LEVEL);
            double xpMultiplier = 1;

            if(hasNode(SALVAGEABLES, salvageChildNodeName, XP_MULTIPLIER))
                xpMultiplier = getDoubleValue(SALVAGEABLES, salvageChildNodeName, XP_MULTIPLIER);

            // Maximum Quantity
            int maximumQuantity = SkillUtils.getRepairAndSalvageQuantities(new ItemStack(itemMaterial), salvageMaterial, salvageMetadata);

            if(hasNode(SALVAGEABLES, salvageChildNodeName, MAXIMUM_QUANTITY))
                maximumQuantity = getIntValue(SALVAGEABLES, salvageChildNodeName, MAXIMUM_QUANTITY);


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
    }

}
