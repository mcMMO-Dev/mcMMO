package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomBlock;

public class CustomBlockConfig extends ConfigLoader {
    private static CustomBlockConfig instance;

    public List<MaterialData> customExcavationBlocks  = new ArrayList<MaterialData>();
    public List<MaterialData> customHerbalismBlocks   = new ArrayList<MaterialData>();
    public List<MaterialData> customMiningBlocks      = new ArrayList<MaterialData>();
    public List<MaterialData> customWoodcuttingBlocks = new ArrayList<MaterialData>();
    public List<MaterialData> customOres              = new ArrayList<MaterialData>();
    public List<MaterialData> customLogs              = new ArrayList<MaterialData>();
    public List<MaterialData> customLeaves            = new ArrayList<MaterialData>();
    public List<MaterialData> customAbilityBlocks     = new ArrayList<MaterialData>();
    public List<MaterialData> customItems             = new ArrayList<MaterialData>();

    public HashMap<MaterialData, CustomBlock> customBlockMap = new HashMap<MaterialData, CustomBlock>();

    public CustomBlockConfig() {
        super("ModConfigs", "blocks.yml");
        loadKeys();
    }

    public static CustomBlockConfig getInstance() {
        if (instance == null) {
            instance = new CustomBlockConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        loadBlocks("Excavation", customExcavationBlocks);
        loadBlocks("Herbalism", customHerbalismBlocks);
        loadBlocks("Mining", customMiningBlocks);
        loadBlocks("Woodcutting", customWoodcuttingBlocks);
        loadBlocks("Ability_Blocks", customAbilityBlocks);
    }

    private void loadBlocks(String skillType, List<MaterialData> blockList) {
        ConfigurationSection skillSection = config.getConfigurationSection(skillType);

        if (skillSection == null) {
            return;
        }

        Set<String> skillConfigSet = skillSection.getKeys(false);

        for (String blockName : skillConfigSet) {
            String[] blockInfo = blockName.split("[|]");

            Material blockMaterial = Material.matchMaterial(blockInfo[0]);

            if (blockMaterial == null) {
                plugin.getLogger().warning("Invalid material name. This item will be skipped.");
                continue;
            }

            byte blockData = Byte.valueOf(blockInfo[1]);
            MaterialData blockMaterialData = new MaterialData(blockMaterial, blockData);
            blockList.add(blockMaterialData);

            if (skillType.equals("Ability_Blocks")) {
                continue;
            }

            customItems.add(blockMaterialData);

            int xp = config.getInt(skillType + "." + blockName + ".XP_Gain");
            int tier = config.getInt(skillType + "." + blockName + ".Tier", 1);

            boolean shouldDropItem = config.getBoolean(skillType + "." + blockName + ".Drop_Item");
            Material dropMaterial = Material.matchMaterial(config.getString(skillType + "." + blockName + ".Drop_Item_Name"));

            if (shouldDropItem && dropMaterial == null) {
                plugin.getLogger().warning("Incomplete item drop information. This block will drop itself.");
                shouldDropItem = false;
            }

            ItemStack itemDrop;

            if (shouldDropItem) {
                byte dropData = (byte) config.getInt(skillType + "." + blockName + ".Drop_Item_Data_Value");
                itemDrop = (new MaterialData(dropMaterial, dropData)).toItemStack(1);
            }
            else {
                itemDrop = blockMaterialData.toItemStack(1);
            }

            int minimumDropAmount = config.getInt(skillType + "." + blockName + ".Min_Drop_Item_Amount", 1);
            int maxiumDropAmount = config.getInt(skillType + "." + blockName + ".Max_Drop_Item_Amount", 1);

            CustomBlock block = new CustomBlock(minimumDropAmount, maxiumDropAmount, itemDrop, tier, xp, blockData, blockMaterial);

            if (skillType.equals("Mining") && config.getBoolean(skillType + "." + blockName + ".Is_Ore")) {
                customOres.add(blockMaterialData);
            }
            else if (skillType.equals("Woodcutting")) {
                if (config.getBoolean(skillType + "." + blockName + ".Is_Log")) {
                    customLogs.add(blockMaterialData);
                }
                else {
                    customLeaves.add(blockMaterialData);
                    block.setXpGain(0); // Leaves don't grant XP
                }
            }

            customBlockMap.put(blockMaterialData, block);
        }
    }
}
