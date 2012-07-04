package com.gmail.nossr50.util;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomBlocksConfig;
import com.gmail.nossr50.config.mods.CustomToolsConfig;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.mods.CustomItem;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public class ModChecks {
    private static Config configInstance = Config.getInstance();
    private static boolean customToolsEnabled = configInstance.getToolModsEnabled();
    private static boolean customArmorEnabled = configInstance.getArmorModsEnabled();
    private static boolean customBlocksEnabled = configInstance.getBlockModsEnabled();

    private static CustomToolsConfig toolInstance = CustomToolsConfig.getInstance();
    private static CustomArmorConfig armorInstance = CustomArmorConfig.getInstance();
    private static CustomBlocksConfig blocksInstance = CustomBlocksConfig.getInstance();

    /**
     * Get the custom armor associated with an item.
     *
     * @param item The item to check
     * @return the armor if it exists, null otherwise
     */
    public static CustomItem getArmorFromItemStack(ItemStack item) {
        return armorInstance.customArmor.get(item.getTypeId());
    }

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the tool if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        return toolInstance.customTools.get(item.getTypeId());
    }

    /**
     * Get the custom block associated with an block.
     *
     * @param block The block to check
     * @return the block if it exists, null otherwise
     */
    public static CustomBlock getCustomBlock(Block block) {
        if (!blocksInstance.customItems.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return null;
        }

        for (CustomBlock b : blocksInstance.customBlocks) {
            if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                return b;
            }
        }

        return null;
    }

    /**
     * Check if a custom block is a custom block.
     *
     * @param block The block to check
     * @return true if the block is custom, false otherwise
     */
    public static boolean isCustomMiningBlock(Block block) {
        if (customBlocksEnabled && blocksInstance.customMiningBlocks.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if a custom block is a leaf block.
     *
     * @param block The block to check
     * @return true if the block represents leaves, false otherwise
     */
    public static boolean isCustomLeafBlock(Block block) {
        if (blocksInstance.customLeaves.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if a custom block is a log block.
     *
     * @param block The block to check
     * @return true if the block represents a log, false otherwise
     */
    public static boolean isCustomLogBlock(Block block) {
        if (blocksInstance.customLogs.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check if a custom block is an ore block.
     *
     * @param block The block to check
     * @return true if the block represents an ore, false otherwise
     */
    public static boolean isCustomOreBlock(Block block) {
        if (blocksInstance.customOres.contains(new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks to see if an item is a custom tool.
     *
     * @param is Item to check
     * @return true if the item is a custom tool, false otherwise
     */
    public static boolean isCustomTool(ItemStack item) {
        if (customToolsEnabled && toolInstance.customTools.containsKey(item.getTypeId())) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks to see if an item is custom armor.
     *
     * @param is Item to check
     * @return true if the item is custom armor, false otherwise
     */
    public static boolean isCustomArmor(ItemStack item) {
        if (customArmorEnabled && armorInstance.customArmor.containsKey(item.getTypeId())) {
            return true;
        }
        else {
            return false;
        }
    }
}
