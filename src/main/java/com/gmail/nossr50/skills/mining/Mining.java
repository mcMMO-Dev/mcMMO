package com.gmail.nossr50.skills.mining;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Skills;

public class Mining {
    private static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private static Config config  = Config.getInstance();

    public static final int DOUBLE_DROPS_MAX_BONUS_LEVEL = advancedConfig.getMiningDoubleDropMaxLevel();
    public static final int DOUBLE_DROPS_MAX_CHANCE = advancedConfig.getMiningDoubleDropChance();

    public static final int DIAMOND_TOOL_TIER = 4;
    public static final int IRON_TOOL_TIER = 3;
    public static final int STONE_TOOL_TIER = 2;

    /**
     * Award XP for Mining blocks.
     *
     * @param player The player to award XP to
     * @param block The block to award XP for
     */
     protected static void miningXP(Player player, PlayerProfile profile, Block block, Material type) {
        int xp = 0;

        switch (type) {
        case COAL_ORE:
            xp += config.getMiningXPCoalOre();
            break;

        case DIAMOND_ORE:
            xp += config.getMiningXPDiamondOre();
            break;

        case ENDER_STONE:
            xp += config.getMiningXPEndStone();
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            xp += config.getMiningXPRedstoneOre();
            break;

        case GLOWSTONE:
            xp += config.getMiningXPGlowstone();
            break;

        case GOLD_ORE:
            xp += config.getMiningXPGoldOre();
            break;

        case IRON_ORE:
            xp += config.getMiningXPIronOre();
            break;

        case LAPIS_ORE:
            xp += config.getMiningXPLapisOre();
            break;

        case MOSSY_COBBLESTONE:
            xp += config.getMiningXPMossyStone();
            break;

        case NETHERRACK:
            xp += config.getMiningXPNetherrack();
            break;

        case OBSIDIAN:
            xp += config.getMiningXPObsidian();
            break;

        case SANDSTONE:
            xp += config.getMiningXPSandstone();
            break;

        case STONE:
            xp += config.getMiningXPStone();
            break;

        case EMERALD_ORE:
            xp += config.getMiningXPEmeraldOre();
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                xp += ModChecks.getCustomBlock(block).getXpGain();
            }
            break;
        }

        Skills.xpProcessing(player, profile, SkillType.MINING, xp);
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

        case COAL_ORE:
            if (config.getCoalDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case DIAMOND_ORE:
            if (config.getDiamondDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (config.getRedstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWSTONE:
            if (config.getGlowstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case LAPIS_ORE:
            if (config.getLapisDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case STONE:
            if (config.getStoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case EMERALD_ORE:
            if (config.getEmeraldDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                ItemStack dropItem = (new MaterialData(block.getTypeId(), block.getData())).toItemStack(1);

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
        ItemStack item = new ItemStack(type);

        switch (type) {
        case COAL_ORE:
            if (config.getCoalDoubleDropsEnabled()) {
                item = (new MaterialData(Material.COAL, CoalType.COAL.getData())).toItemStack(1);

                Misc.dropItem(location, item);
            }
            break;

        case DIAMOND_ORE:
            if (config.getDiamondDoubleDropsEnabled()) {
                item = new ItemStack(Material.DIAMOND);
                Misc.dropItem(location, item);
            }
            break;

        case ENDER_STONE:
            if (config.getEndStoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (config.getRedstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.REDSTONE);
                Misc.dropItems(location, item, 4);
                Misc.randomDropItem(location, item, 50);
            }
            break;

        case GLOWSTONE:
            if (config.getGlowstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                Misc.dropItems(location, item, 2);
                Misc.randomDropItems(location, item, 50, 2);
            }
            break;

        case GOLD_ORE:
            if (config.getGoldDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case IRON_ORE:
            if (config.getIronDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case LAPIS_ORE:
            if (config.getLapisDoubleDropsEnabled()) {
                try {
                    item = (new MaterialData(Material.INK_SACK, DyeColor.BLUE.getDyeData())).toItemStack(1);
                }
                catch(Exception e) {
                    item = (new MaterialData(Material.INK_SACK, (byte) 4)).toItemStack(1);
                }
                catch(NoSuchMethodError e) {
                    item = (new MaterialData(Material.INK_SACK, (byte) 4)).toItemStack(1);
                }

                Misc.dropItems(location, item, 4);
                Misc.randomDropItems(location, item, 50, 4);
            }
            break;

        case MOSSY_COBBLESTONE:
            if (config.getMossyCobblestoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case NETHERRACK:
            if (config.getNetherrackDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case OBSIDIAN:
            if (config.getObsidianDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case SANDSTONE:
            if (config.getSandstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case STONE:
            if (config.getStoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.COBBLESTONE);
                Misc.dropItem(location, item);
            }
            break;

        case EMERALD_ORE:
            if (config.getEmeraldDoubleDropsEnabled()) {
                item = new ItemStack(Material.EMERALD);
                Misc.dropItem(location, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                CustomBlock customBlock = ModChecks.getCustomBlock(block);
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();

                item = ModChecks.getCustomBlock(block).getItemDrop();

                if (minimumDropAmount != maximumDropAmount) {
                    Misc.dropItems(location, item, minimumDropAmount);
                    Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
                }
                else {
                    Misc.dropItems(location, item, minimumDropAmount);
                }
            }
            break;
        }
    }
}
