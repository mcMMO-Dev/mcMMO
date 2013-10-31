package com.gmail.nossr50.util;


import java.io.File;

import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.mods.CustomBlockConfig;
import com.gmail.nossr50.config.mods.CustomEntityConfig;
import com.gmail.nossr50.config.mods.CustomToolConfig;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.mods.CustomEntity;
import com.gmail.nossr50.datatypes.mods.CustomTool;


public final class ModUtils {
    private static boolean customToolsEnabled    = Config.getInstance().getToolModsEnabled();
    private static boolean customBlocksEnabled   = Config.getInstance().getBlockModsEnabled();
    private static boolean customEntitiesEnabled = Config.getInstance().getEntityModsEnabled();

    private ModUtils() {}

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the tool if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        return CustomToolConfig.getInstance().getCustomTool(item.getType());
    }

    /**
     * Get the custom entity associated with an entity.
     *
     * @param entity The entity to check
     * @return the entity is if exists, null otherwise
     */
    public static CustomEntity getCustomEntity(Entity entity) {
        return CustomEntityConfig.getInstance().getCustomEntity(entity);
    }

    /**
     * Get the custom block associated with an block.
     *
     * @param blockState The BlockState of the bloc to check
     * @return the block if it exists, null otherwise
     */
    public static CustomBlock getCustomBlock(BlockState blockState) {
        return CustomBlockConfig.getInstance().getCustomBlock(blockState.getData());
    }

    public static CustomBlock getCustomSmeltingBlock(ItemStack smelting) {
        return CustomBlockConfig.getInstance().getCustomBlock(smelting.getData());
    }

    /**
     * Check if a custom block is a woodcutting block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom woodcutting block, false otherwise
     */
    public static boolean isCustomWoodcuttingBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomWoodcuttingBlock(blockState.getData());
    }

    /**
     * Check if a custom block should not activate abilites.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents an ability block, false otherwise
     */
    public static boolean isCustomAbilityBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomAbilityBlock(blockState.getData());
    }

    /**
     * Check if a custom block is a mining block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom mining block, false otherwise
     */
    public static boolean isCustomMiningBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomMiningBlock(blockState.getData());
    }

    /**
     * Check if a custom block is an excavation block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom excavation block, false otherwise
     */
    public static boolean isCustomExcavationBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomExcavationBlock(blockState.getData());
    }

    /**
     * Check if a custom block is an herbalism block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a custom herbalism block, false otherwise
     */
    public static boolean isCustomHerbalismBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomHerbalismBlock(blockState.getData());
    }

    /**
     * Check if a custom block is a leaf block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents leaves, false otherwise
     */
    public static boolean isCustomLeafBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomLeaf(blockState.getData());
    }

    /**
     * Check if a custom block is a log block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents a log, false otherwise
     */
    public static boolean isCustomLogBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomLog(blockState.getData());
    }

    /**
     * Check if a custom block is an ore block.
     *
     * @param blockState The BlockState of the block to check
     * @return true if the block represents an ore, false otherwise
     */
    public static boolean isCustomOreBlock(BlockState blockState) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomOre(blockState.getData());
    }

    /**
     * Check if a custom block is an ore block.
     *
     * @param item The ItemStack of the block to check
     * @return true if the block represents an ore, false otherwise
     */
    public static boolean isCustomOreBlock(ItemStack item) {
        return customBlocksEnabled && CustomBlockConfig.getInstance().isCustomOre(item.getData());
    }

    /**
     * Checks to see if an item is a custom tool.
     *
     * @param item Item to check
     * @return true if the item is a custom tool, false otherwise
     */
    public static boolean isCustomTool(ItemStack item) {
        return customToolsEnabled && CustomToolConfig.getInstance().isCustomTool(item.getType());
    }

    /**
     * Checks to see if an entity is a custom entity.
     *
     * @param entity Entity to check
     * @return true if the entity is a custom entity, false otherwise
     */
    public static boolean isCustomEntity(Entity entity) {
        return customEntitiesEnabled && CustomEntityConfig.getInstance().isCustomEntity(entity);
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

    public static void addCustomEntity(Entity entity) {
        if (!customEntitiesEnabled) {
            return;
        }

        File entityFile = CustomEntityConfig.getInstance().getFile();
        YamlConfiguration entitiesFile = YamlConfiguration.loadConfiguration(entityFile);

        String entityName = entity.getType().toString();
        String sanitizedEntityName = entityName.replace(".", "_");

        if (entitiesFile.getKeys(false).contains(sanitizedEntityName)) {
            return;
        }

        entitiesFile.set(sanitizedEntityName + ".XP_Multiplier", 1.0D);
        entitiesFile.set(sanitizedEntityName + ".Tameable", false);
        entitiesFile.set(sanitizedEntityName + ".Taming_XP", 0);
        entitiesFile.set(sanitizedEntityName + ".CanBeSummoned", false);
        entitiesFile.set(sanitizedEntityName + ".COTW_Material", "");
        entitiesFile.set(sanitizedEntityName + ".COTW_Material_Data", 0);
        entitiesFile.set(sanitizedEntityName + ".COTW_Material_Amount", 0);

        String className = "";

        try {
            className = ((Class<?>) entity.getClass().getDeclaredField("entityClass").get(entity)).getName();
        }
        catch (Exception e) {
            if (e instanceof NoSuchFieldException || e instanceof IllegalArgumentException || e instanceof IllegalAccessException) {
                className = entity.getClass().getName();
            }
            else {
                e.printStackTrace();
            }
        }

        CustomEntityConfig.getInstance().addEntity(new CustomEntity(1.0D, false, 0, false, null, 0), className, entityName);

        try {
            entitiesFile.save(entityFile);
            mcMMO.p.debug(entity.getType().toString() + " was added to the custom entities file!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
