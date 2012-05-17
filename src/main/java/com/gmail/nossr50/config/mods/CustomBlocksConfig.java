package com.gmail.nossr50.config.mods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.mods.CustomBlock;

public class CustomBlocksConfig extends ModConfigLoader{
    private static CustomBlocksConfig instance;

    public static CustomBlocksConfig getInstance() {
        if (instance == null) {
            instance = new CustomBlocksConfig(mcMMO.p);
        }

        return instance;
    }

    public List<ItemStack> customExcavationBlocks = new ArrayList<ItemStack>();
    public List<ItemStack> customHerbalismBlocks = new ArrayList<ItemStack>();
    public List<ItemStack> customMiningBlocks = new ArrayList<ItemStack>();
    public List<ItemStack> customWoodcuttingBlocks = new ArrayList<ItemStack>();

    public List<ItemStack> customOres = new ArrayList<ItemStack>();
    public List<ItemStack> customLogs = new ArrayList<ItemStack>();
    public List<ItemStack> customLeaves = new ArrayList<ItemStack>();

    public List<ItemStack> customItems = new ArrayList<ItemStack>();
    public List<CustomBlock> customBlocks = new ArrayList<CustomBlock>();

    public CustomBlocksConfig(mcMMO plugin) {
        super(plugin, "blocks.yml");
        config = plugin.getBlocksConfig();
    }

    @Override
    public void load() {
        if (!configFile.exists()) {
            dataFolder.mkdir();
            plugin.saveBlocksConfig();
        }

        addDefaults();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        plugin.getLogger().info("Loading mcMMO blocks.yml File...");

        loadBlocks("Excavation", customExcavationBlocks);
        loadBlocks("Herbalism", customHerbalismBlocks);
        loadBlocks("Mining", customMiningBlocks);
        loadBlocks("Woodcutting", customWoodcuttingBlocks);
    }

    private void loadBlocks(String skillType, List<ItemStack> blockList) {
        ConfigurationSection skillSection = config.getConfigurationSection(skillType);
        Set<String> skillConfigSet = skillSection.getKeys(false);
        Iterator<String> iterator = skillConfigSet.iterator();

        while (iterator.hasNext()) {
            String blockName = iterator.next();

            int id = config.getInt(skillType + "." + blockName + ".ID", 0);
            byte data = (byte) config.getInt(skillType + "." + blockName + ".Data_Value", 0);
            int xp = config.getInt(skillType + "." + blockName + ".XP_Gain", 0);
            boolean dropItem = config.getBoolean(skillType + "." + blockName + ".Drop_Item", false);
            int dropID = config.getInt(skillType + "." + blockName + ".Drop_Item_ID", 0);
            byte dropData = (byte) config.getInt(skillType + "." + blockName + ".Drop_Item_Data_Value", 0);

            if (id == 0) {
                plugin.getLogger().warning("Missing ID. This block will be skipped.");
                continue;
            }

            if (dropItem && dropID == 0) {
                plugin.getLogger().warning("Incomplete item drop information. This block will drop itself.");
                dropItem = false;
            }

            CustomBlock block;
            ItemStack itemDrop;
            ItemStack blockItem;

            if (dropItem) {
                itemDrop = new ItemStack(dropID, 1, (short) 0, dropData);
            }
            else {
                itemDrop = new ItemStack(id, 1, (short) 0, data);
            }

            block = new CustomBlock(itemDrop, xp, data, id);
            blockItem = new ItemStack(id, 1, (short) 0, data);

            if (skillType.equals("Mining") && config.getBoolean(skillType + "." + blockName + ".Is_Ore")) {
                customOres.add(blockItem);
            }
            else if (skillType.equals("Woodcutting")) {
                if (config.getBoolean(skillType + "." + blockName + ".Is_Log")) {
                    customLogs.add(blockItem);
                }
                else {
                    customLeaves.add(blockItem);
                    block.setXpGain(0); //Leaves don't grant XP
                }
            }

            blockList.add(blockItem);
            customItems.add(blockItem);
            customBlocks.add(block);
        }
    }
}
