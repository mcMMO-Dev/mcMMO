package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class Mining {

    /**
     * Calculate XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public static int getBlockXp(BlockState blockState) {
        int xp = ExperienceConfig.getInstance().getXp(PrimarySkillType.MINING, blockState.getType());

        if (xp == 0 && mcMMO.getModManager().isCustomMiningBlock(blockState)) {
            xp = mcMMO.getModManager().getBlock(blockState).getXpGain();
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

        switch (blockType) {
            case ANDESITE:
            case DIORITE:
            case GRANITE:
            case END_STONE:
            case TERRACOTTA:
            case CLAY:
            case IRON_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
            case BLACK_GLAZED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case BLUE_GLAZED_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_GLAZED_TERRACOTTA:
            case BROWN_TERRACOTTA:
            case CYAN_GLAZED_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case GRAY_GLAZED_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case GREEN_GLAZED_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case LIME_GLAZED_TERRACOTTA:
            case LIME_TERRACOTTA:
            case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case PINK_GLAZED_TERRACOTTA:
            case PINK_TERRACOTTA:
            case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case RED_GLAZED_TERRACOTTA:
            case RED_TERRACOTTA:
            case WHITE_GLAZED_TERRACOTTA:
            case WHITE_TERRACOTTA:
            case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_TERRACOTTA:
                handleMiningDrops(blockState);
                return;

            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWSTONE:
            case LAPIS_ORE:
            case PACKED_ICE:
            case NETHER_QUARTZ_ORE:
            case REDSTONE_ORE:
            case STONE:
            case PRISMARINE:
                Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType()));
                return;

            default:
                if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItem(Misc.getBlockCenter(blockState), new ItemStack(blockState.getType()));
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
        switch (blockState.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case END_STONE:
            case GLOWSTONE:
            case GOLD_ORE:
            case TERRACOTTA:
            case IRON_ORE:
            case LAPIS_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case PACKED_ICE:
            case REDSTONE_ORE:
            case SANDSTONE:
            case BLACK_GLAZED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case BLUE_GLAZED_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_GLAZED_TERRACOTTA:
            case BROWN_TERRACOTTA:
            case CYAN_GLAZED_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case GRAY_GLAZED_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case GREEN_GLAZED_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case LIME_GLAZED_TERRACOTTA:
            case LIME_TERRACOTTA:
            case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case PINK_GLAZED_TERRACOTTA:
            case PINK_TERRACOTTA:
            case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case RED_GLAZED_TERRACOTTA:
            case RED_TERRACOTTA:
            case WHITE_GLAZED_TERRACOTTA:
            case WHITE_TERRACOTTA:
            case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_TERRACOTTA:
            case STONE:
            case NETHER_QUARTZ_ORE:
                Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
                return;

            default:
                if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
                }
                return;
        }
    }
}
