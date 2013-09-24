package com.gmail.nossr50.util;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomArmorConfig;
import com.gmail.nossr50.config.mods.CustomBlockConfig;
import com.gmail.nossr50.config.mods.CustomEntityConfig;
import com.gmail.nossr50.config.mods.CustomToolConfig;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.mods.CustomEntity;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public final class ModUtils {
    private static Config configInstance = Config.getInstance();

    private static boolean customToolsEnabled    = configInstance.getToolModsEnabled();
    private static boolean customArmorEnabled    = configInstance.getArmorModsEnabled();
    private static boolean customBlocksEnabled   = configInstance.getBlockModsEnabled();
    private static boolean customEntitiesEnabled = configInstance.getEntityModsEnabled();

    private ModUtils() {}

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the tool if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        return CustomToolConfig.getInstance().customToolMap.get(item.getType());
    }

    /**
     * Get the custom block associated with an block.
     *
     * @param blockState The block to check
     * @return the block if it exists, null otherwise
     */
    public static CustomBlock getCustomBlock(BlockState blockState) {
        return CustomBlockConfig.getInstance().customBlockMap.get(blockState.getData());
    }

    public static CustomEntity getCustomEntity(Entity entity) {
        CustomEntity customEntity = CustomEntityConfig.getInstance().customEntityTypeMap.get(entity.getType().toString());

        if (customEntity == null) {
            try {
                customEntity = CustomEntityConfig.getInstance().customEntityClassMap.get(((Class<?>) entity.getClass().getDeclaredField("entityClass").get(entity)).getName());
            }
            catch (NoSuchFieldException e){
                return null;
            }
            catch (IllegalArgumentException e) {
                return null;
            }
            catch (IllegalAccessException e) {
                return null;
            }
        }

        return customEntity;
    }

    /**
     * Check if a custom block is a woodcutting block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom woodcutting block, false otherwise
     */
    public static boolean isCustomWoodcuttingBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customWoodcuttingBlocks.contains(blockState.getData());
    }

    /**
     * Check if a custom block should not activate abilites.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents an ability block, false otherwise
     */
    public static boolean isCustomAbilityBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customAbilityBlocks.contains(blockState.getData());
    }

    /**
     * Check if a custom block is a mining block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom mining block, false otherwise
     */
    public static boolean isCustomMiningBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customMiningBlocks.contains(blockState.getData());
    }

    /**
     * Check if a custom block is an excavation block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom excavation block, false otherwise
     */
    public static boolean isCustomExcavationBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customExcavationBlocks.contains(blockState.getData());
    }

    /**
     * Check if a custom block is an herbalism block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom herbalism block, false otherwise
     */
    public static boolean isCustomHerbalismBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customHerbalismBlocks.contains(blockState.getData());
    }

    /**
     * Check if a custom block is a leaf block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents leaves, false otherwise
     */
    public static boolean isCustomLeafBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customLeaves.contains(blockState.getData());
    }

    /**
     * Check if a custom block is a log block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a log, false otherwise
     */
    public static boolean isCustomLogBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customLogs.contains(blockState.getData());
    }

    /**
     * Check if a custom block is an ore block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents an ore, false otherwise
     */
    public static boolean isCustomOreBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().customOres.contains(blockState.getData());
    }

    /**
     * Checks to see if an item is a custom tool.
     *
     * @param item Item to check
     * @return true if the item is a custom tool, false otherwise
     */
    public static boolean isCustomTool(ItemStack item) {
        return customToolsEnabled && CustomToolConfig.getInstance().customTool.contains(item.getType());
    }

    /**
     * Checks to see if an item is custom armor.
     *
     * @param item Item to check
     * @return true if the item is custom armor, false otherwise
     */
    public static boolean isCustomArmor(ItemStack item) {
        return customArmorEnabled && CustomArmorConfig.getInstance().customArmor.contains(item.getType());
    }

    public static boolean isCustomEntity(Entity entity) {
        if (!customEntitiesEnabled) {
            return false;
        }

        if (CustomEntityConfig.getInstance().customEntityTypeMap.containsKey(entity.getType().toString())) {
            return true;
        }

        try {
            return CustomEntityConfig.getInstance().customEntityClassMap.containsKey(((Class<?>) entity.getClass().getDeclaredField("entityClass").get(entity)).getName());
        }
        catch (NoSuchFieldException e){
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        catch (IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Check if a custom entity is a boss.
     *
     * @param entity The entity to check
     * @return true if the entity represents a boss, false otherwise
     */
    public static boolean isCustomBossEntity(Entity entity) {
        //TODO: Finish this method
        return false;
    }
}
