package com.gmail.nossr50.mods;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.mods.config.CustomArmorConfig;
import com.gmail.nossr50.mods.config.CustomBlocksConfig;
import com.gmail.nossr50.mods.config.CustomEntityConfig;
import com.gmail.nossr50.mods.config.CustomToolsConfig;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.mods.datatypes.CustomEntity;
import com.gmail.nossr50.mods.datatypes.CustomItem;
import com.gmail.nossr50.mods.datatypes.CustomTool;

public final class ModChecks {
    private static Config configInstance = Config.getInstance();
    private static boolean customToolsEnabled = configInstance.getToolModsEnabled();
    private static boolean customArmorEnabled = configInstance.getArmorModsEnabled();
    private static boolean customBlocksEnabled = configInstance.getBlockModsEnabled();
    private static boolean customEntitiesEnabled = configInstance.getEntityModsEnabled();

    private ModChecks() {}

    /**
     * Get the custom armor associated with an item.
     *
     * @param item The item to check
     * @return the armor if it exists, null otherwise
     */
    public static CustomItem getArmorFromItemStack(ItemStack item) {
        return CustomArmorConfig.getInstance().customArmor.get(item.getTypeId());
    }

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the tool if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        return CustomToolsConfig.getInstance().customTools.get(item.getTypeId());
    }

    /**
     * Get the custom block associated with an block.
     *
     * @param block The block to check
     * @return the block if it exists, null otherwise
     */
    public static CustomBlock getCustomBlock(Block block) {
        if (!Config.getInstance().getBlockModsEnabled()) {
            return null;
        }

        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (!CustomBlocksConfig.getInstance().customItems.contains(item)) {
            return null;
        }

        for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
            if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                return b;
            }
        }

        return null;
    }

    public static CustomEntity getCustomEntity(Entity entity) {
        if (!CustomEntityConfig.getInstance().customEntityIds.contains(entity.getEntityId()) && !CustomEntityConfig.getInstance().customEntityTypes.contains(entity.getType())) {
            return null;
        }

        for (CustomEntity customEntity : CustomEntityConfig.getInstance().customEntities) {
            if ((customEntity.getEntityID() == entity.getEntityId()) && (customEntity.getEntityType() == entity.getType())) {
                return customEntity;
            }
        }

        return null;
    }

    /**
     * Check if a custom block is a mining block.
     *
     * @param block The block to check
     * @return true if the block is custom, false otherwise
     */
    public static boolean isCustomMiningBlock(Block block) {
        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (customBlocksEnabled && CustomBlocksConfig.getInstance().customMiningBlocks.contains(item)) {
            for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
                if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a custom block is a mining block.
     *
     * @param block The block to check
     * @return true if the block is custom, false otherwise
     */
    public static boolean isCustomExcavationBlock(Block block) {
        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (customBlocksEnabled && CustomBlocksConfig.getInstance().customExcavationBlocks.contains(item)) {
            for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
                if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a custom block is a leaf block.
     *
     * @param block The block to check
     * @return true if the block represents leaves, false otherwise
     */
    public static boolean isCustomLeafBlock(Block block) {
        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (CustomBlocksConfig.getInstance().customLeaves.contains(item)) {
            for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
                if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a custom block is a log block.
     *
     * @param block The block to check
     * @return true if the block represents a log, false otherwise
     */
    public static boolean isCustomLogBlock(Block block) {
        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (CustomBlocksConfig.getInstance().customLogs.contains(item)) {
            for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
                if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a custom block is an ore block.
     *
     * @param block The block to check
     * @return true if the block represents an ore, false otherwise
     */
    public static boolean isCustomOreBlock(Block block) {
        ItemStack item = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

        if (CustomBlocksConfig.getInstance().customOres.contains(item)) {
            for (CustomBlock b : CustomBlocksConfig.getInstance().customBlocks) {
                if ((b.getItemID() == block.getTypeId()) && (b.getDataValue() == block.getData())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks to see if an item is a custom tool.
     *
     * @param item Item to check
     * @return true if the item is a custom tool, false otherwise
     */
    public static boolean isCustomTool(ItemStack item) {
        if (customToolsEnabled && CustomToolsConfig.getInstance().customTools.containsKey(item.getTypeId())) {
            return true;
        }

        return false;
    }

    /**
     * Checks to see if an item is custom armor.
     *
     * @param item Item to check
     * @return true if the item is custom armor, false otherwise
     */
    public static boolean isCustomArmor(ItemStack item) {
        if (customArmorEnabled && CustomArmorConfig.getInstance().customArmor.containsKey(item.getTypeId())) {
            return true;
        }

        return false;
    }

    public static boolean isCustomEntity(Entity entity) {
        if (customEntitiesEnabled && CustomEntityConfig.getInstance().customEntityIds.contains(entity.getEntityId())) {
            return true;
        }

        return false;
    }
}
