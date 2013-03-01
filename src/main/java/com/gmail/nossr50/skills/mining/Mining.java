package com.gmail.nossr50.skills.mining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;

public class Mining {
    public static int    doubleDropsMaxLevel  = AdvancedConfig.getInstance().getMiningDoubleDropMaxLevel();
    public static double doubleDropsMaxChance = AdvancedConfig.getInstance().getMiningDoubleDropChance();

    /**
     * Calculate XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    protected static int getBlockXp(BlockState blockState) {
        Material blockType = blockState.getType();
        int xp = Config.getInstance().getXp(SkillType.MINING, blockType);

        if (blockType == Material.GLOWING_REDSTONE_ORE) {
            xp = Config.getInstance().getXp(SkillType.MINING, Material.REDSTONE_ORE);
        }
        else if (xp == 0 && ModUtils.isCustomMiningBlock(blockState)) {
            xp = ModUtils.getCustomBlock(blockState).getXpGain();
        }

        return xp;
    }

    /**
     * Handle double drops when using Silk Touch.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    protected static void handleSilkTouchDrops(BlockState blockState) {
        Material blockType = blockState.getType();

        if (blockType != Material.GLOWING_REDSTONE_ORE && !Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, blockType)) {
            return;
        }

        switch (blockType) {
            case ENDER_STONE:
            case GOLD_ORE:
            case IRON_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
                handleMiningDrops(blockState);
                return;

            case GLOWING_REDSTONE_ORE:
                if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                    Misc.dropItem(blockState.getLocation(), new ItemStack(Material.REDSTONE_ORE));
                }
                return;

            case COAL_ORE:
            case DIAMOND_ORE:
            case REDSTONE_ORE:
            case GLOWSTONE:
            case LAPIS_ORE:
            case STONE:
            case EMERALD_ORE:
                Misc.dropItem(blockState.getLocation(), new ItemStack(blockType));
                return;

            default:
                if (ModUtils.isCustomMiningBlock(blockState)) {
                    Misc.dropItem(blockState.getLocation(), blockState.getData().toItemStack());
                }
                return;
        }
    }

    /**
     * Handle double drops from Mining & Blast Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    protected static void handleMiningDrops(BlockState blockState) {
        Material blockType = blockState.getType();

        if (blockType != Material.GLOWING_REDSTONE_ORE && !Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, blockType)) {
            return;
        }

        Location location = blockState.getLocation();
        ItemStack dropItem;

        switch (blockType) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWSTONE:
            case LAPIS_ORE:
            case STONE:
            case ENDER_STONE:
            case GOLD_ORE:
            case IRON_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
                for (ItemStack drop : blockState.getBlock().getDrops()) {
                    Misc.dropItem(location, drop);
                }
                return;

            case GLOWING_REDSTONE_ORE:
            case REDSTONE_ORE:
                if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                    for (ItemStack drop : blockState.getBlock().getDrops()) {
                        Misc.dropItem(location, drop);
                    }
                }
                return;
            default:
                if (ModUtils.isCustomMiningBlock(blockState)) {
                    CustomBlock customBlock = ModUtils.getCustomBlock(blockState);
                    int minimumDropAmount = customBlock.getMinimumDropAmount();
                    int maximumDropAmount = customBlock.getMaximumDropAmount();

                    dropItem = customBlock.getItemDrop();

                    if (minimumDropAmount != maximumDropAmount) {
                        Misc.dropItems(location, dropItem, minimumDropAmount);
                        Misc.randomDropItems(location, dropItem, maximumDropAmount - minimumDropAmount);
                    }
                    else {
                        Misc.dropItems(location, dropItem, minimumDropAmount);
                    }
                }
                return;
        }
    }
}
