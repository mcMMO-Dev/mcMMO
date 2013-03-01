package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.mods.CustomBlock;

public class CustomBlockConfig extends ConfigLoader {
    private static CustomBlockConfig instance;

    public List<ItemStack> customExcavationBlocks  = new ArrayList<ItemStack>();
    public List<ItemStack> customHerbalismBlocks   = new ArrayList<ItemStack>();
    public List<ItemStack> customMiningBlocks      = new ArrayList<ItemStack>();
    public List<ItemStack> customWoodcuttingBlocks = new ArrayList<ItemStack>();
    public List<ItemStack> customOres              = new ArrayList<ItemStack>();
    public List<ItemStack> customLogs              = new ArrayList<ItemStack>();
    public List<ItemStack> customLeaves            = new ArrayList<ItemStack>();
    public List<ItemStack> customAbilityBlocks     = new ArrayList<ItemStack>();
    public List<ItemStack> customItems             = new ArrayList<ItemStack>();

    public List<CustomBlock> customBlocks = new ArrayList<CustomBlock>();

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

    private void loadBlocks(String skillType, List<ItemStack> blockList) {
        ConfigurationSection skillSection = config.getConfigurationSection(skillType);

        if (skillSection == null) {
            return;
        }

        Set<String> skillConfigSet = skillSection.getKeys(false);

        for (String blockName : skillConfigSet) {
            int id = config.getInt(skillType + "." + blockName + ".ID", 0);
            byte data = (byte) config.getInt(skillType + "." + blockName + ".Data_Value", 0);
            int xp = config.getInt(skillType + "." + blockName + ".XP_Gain", 0);
            int tier = config.getInt(skillType + "." + blockName + ".Tier", 1);
            boolean dropItem = config.getBoolean(skillType + "." + blockName + ".Drop_Item", false);
            int dropID = config.getInt(skillType + "." + blockName + ".Drop_Item_ID", 0);
            byte dropData = (byte) config.getInt(skillType + "." + blockName + ".Drop_Item_Data_Value", 0);
            int minimumDropAmount = config.getInt(skillType + "." + blockName + ".Min_Drop_Item_Amount", 1);
            int maxiumDropAmount = config.getInt(skillType + "." + blockName + ".Max_Drop_Item_Amount", 1);

            CustomBlock block;
            ItemStack itemDrop;
            ItemStack blockItem;

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This block will be skipped.");
                continue;
            }

            if (skillType.equals("Ability_Blocks")) {
                blockItem = (new MaterialData(id, data)).toItemStack(1);

                blockList.add(blockItem);
                continue;
            }

            if (dropItem && dropID == 0) {
                plugin.getLogger().warning("Incomplete item drop information. This block will drop itself.");
                dropItem = false;
            }

            if (dropItem) {
                itemDrop = (new MaterialData(dropID, dropData)).toItemStack(1);
            }
            else {
                itemDrop = (new MaterialData(id, data)).toItemStack(1);
            }

            block = new CustomBlock(minimumDropAmount, maxiumDropAmount, itemDrop, tier, xp, data, id);
            blockItem = (new MaterialData(id, data)).toItemStack(1);

            if (skillType.equals("Mining") && config.getBoolean(skillType + "." + blockName + ".Is_Ore")) {
                customOres.add(blockItem);
            }
            else if (skillType.equals("Woodcutting")) {
                if (config.getBoolean(skillType + "." + blockName + ".Is_Log")) {
                    customLogs.add(blockItem);
                }
                else {
                    customLeaves.add(blockItem);
                    block.setXpGain(0); // Leaves don't grant XP
                }
            }

            blockList.add(blockItem);
            customItems.add(blockItem);
            customBlocks.add(block);
        }
    }
}
