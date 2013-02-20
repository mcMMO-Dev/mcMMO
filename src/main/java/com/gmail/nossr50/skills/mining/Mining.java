package com.gmail.nossr50.skills.mining;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;

public class Mining {
    private static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    public static int doubleDropsMaxLevel = advancedConfig.getMiningDoubleDropMaxLevel();
    public static double doubleDropsMaxChance = advancedConfig.getMiningDoubleDropChance();

    public static final int DIAMOND_TOOL_TIER = 4;
    public static final int IRON_TOOL_TIER = 3;
    public static final int STONE_TOOL_TIER = 2;

    /**
     * Award XP for Mining blocks.
     *
     * @param mcMMOPlayer The player to award XP to
     * @param block The block to award XP for
     */
    protected static void miningXP(McMMOPlayer mcMMOPlayer, Block block, Material type) {
        int xp = Config.getInstance().getXp(SkillType.MINING, type);

        if (type == Material.GLOWING_REDSTONE_ORE) {
            xp = Config.getInstance().getXp(SkillType.MINING, Material.REDSTONE_ORE);
        }
        else if (xp == 0 && ModChecks.isCustomMiningBlock(block)) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }

        mcMMOPlayer.beginXpGain(SkillType.MINING, xp);
    }

    /**
     * Handle double drops when using Silk Touch.
     *
     * @param block The block to process drops for
     * @param location The location of the block
     * @param type The material type of the block
     */
    protected static void silkTouchDrops(Block block, Location location, Material type) {
        ItemStack item = new ItemStack(type);

        if (type != Material.GLOWING_REDSTONE_ORE && !Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, type)) {
            return;
        }

        switch (type) {
        case ENDER_STONE:
        case GOLD_ORE:
        case IRON_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case SANDSTONE:
            miningDrops(block, location, type);
            break;

        case GLOWING_REDSTONE_ORE:
            if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                Misc.dropItem(location, item);
            }
            break;

        case COAL_ORE:
        case DIAMOND_ORE:
        case REDSTONE_ORE:
        case GLOWSTONE:
        case LAPIS_ORE:
        case STONE:
        case EMERALD_ORE:
            Misc.dropItem(location, item);
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                ItemStack dropItem = new ItemStack(block.getTypeId(), 1, block.getData());

                Misc.dropItem(location, dropItem);
            }
            break;
        }
    }

    /**
     * Drop items from Mining & Blast Mining skills.
     *
     * @param block The block to process drops for
     * @param location The location of the block
     * @param type The material type of the block
     */
    protected static void miningDrops(Block block, Location location, Material type) {
        if (type != Material.GLOWING_REDSTONE_ORE && !Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, type)) {
            return;
        }

        ItemStack item = new ItemStack(type);

        switch (type) {
        case COAL_ORE:
            item = new ItemStack(Material.COAL, 1, CoalType.COAL.getData());
            Misc.dropItem(location, item);
            break;

        case DIAMOND_ORE:
            item = new ItemStack(Material.DIAMOND);
            Misc.dropItem(location, item);
            break;

        case EMERALD_ORE:
            item = new ItemStack(Material.EMERALD);
            Misc.dropItem(location, item);
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                item = new ItemStack(Material.REDSTONE);
                Misc.dropItems(location, item, 4);
                Misc.randomDropItem(location, item, 50);
            }
            break;

        case GLOWSTONE:
            item = new ItemStack(Material.GLOWSTONE_DUST);
            Misc.dropItems(location, item, 2);
            Misc.randomDropItems(location, item, 2);
            break;

        case LAPIS_ORE:
            item = new ItemStack(Material.INK_SACK, 1, DyeColor.BLUE.getDyeData());
            Misc.dropItems(location, item, 4);
            Misc.randomDropItems(location, item, 4);
            break;

        case STONE:
            item = new ItemStack(Material.COBBLESTONE);
            Misc.dropItem(location, item);
            break;

        case ENDER_STONE:
        case GOLD_ORE:
        case IRON_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case SANDSTONE:
            Misc.dropItem(location, item);
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                CustomBlock customBlock = ModChecks.getCustomBlock(block);
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();

                item = ModChecks.getCustomBlock(block).getItemDrop();

                if (minimumDropAmount != maximumDropAmount) {
                    Misc.dropItems(location, item, minimumDropAmount);
                    Misc.randomDropItems(location, item, maximumDropAmount - minimumDropAmount);
                }
                else {
                    Misc.dropItems(location, item, minimumDropAmount);
                }
            }
            break;
        }
    }
}
