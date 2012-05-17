package com.gmail.nossr50.util;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.mods.CustomBlocksConfig;
import com.gmail.nossr50.config.mods.LoadCustomArmor;
import com.gmail.nossr50.config.mods.LoadCustomTools;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.mods.CustomItem;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public class ModChecks {
    private static LoadCustomTools toolInstance = LoadCustomTools.getInstance();
    private static LoadCustomArmor armorInstance = LoadCustomArmor.getInstance();
    private static CustomBlocksConfig blocksInstance = CustomBlocksConfig.getInstance();

    /**
     * Get the custom armor associated with an item.
     *
     * @param item The item to check
     * @return the ay if it exists, null otherwise
     */
    public static CustomItem getArmorFromItemStack(ItemStack item) {
        int id = item.getTypeId();

        if (!armorInstance.customIDs.contains(id)) {
            return null;
        }

        for (CustomItem armor : armorInstance.customItems) {
            if (armor.getItemID() == id) {
                return armor;
            }
        }

        return null;
    }

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the armor if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        int id = item.getTypeId();

        if (!toolInstance.customIDs.contains(id)) {
            return null;
        }

        for (CustomItem tool : toolInstance.customItems) {
            if (tool.getItemID() == id) {
                return (CustomTool) tool;
            }
        }

        return null;
    }

    /**
     * Get the custom block associated with an block.
     *
     * @param block The block to check
     * @return the armor if it exists, null otherwise
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
}
