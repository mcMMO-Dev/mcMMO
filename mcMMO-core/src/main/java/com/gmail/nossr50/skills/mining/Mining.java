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
            case Material.END_STONE:
            case Material.TERRACOTTA:
            case Material.CLAY:
            case Material.IRON_ORE:
            case Material.MOSSY_COBBLESTONE:
            case Material.NETHERRACK:
            case Material.OBSIDIAN:
            case Material.SANDSTONE:
            case Material.BLACK_GLAZED_TERRACOTTA:
            case Material.BLACK_TERRACOTTA:
            case Material.BLUE_GLAZED_TERRACOTTA:
            case Material.BLUE_TERRACOTTA:
            case Material.BROWN_GLAZED_TERRACOTTA:
            case Material.BROWN_TERRACOTTA:
            case Material.CYAN_GLAZED_TERRACOTTA:
            case Material.CYAN_TERRACOTTA:
            case Material.GRAY_GLAZED_TERRACOTTA:
            case Material.GRAY_TERRACOTTA:
            case Material.GREEN_GLAZED_TERRACOTTA:
            case Material.GREEN_TERRACOTTA:
            case Material.LIGHT_BLUE_GLAZED_TERRACOTTA:
            case Material.LIGHT_BLUE_TERRACOTTA:
            case Material.LIGHT_GRAY_GLAZED_TERRACOTTA:
            case Material.LIGHT_GRAY_TERRACOTTA:
            case Material.LIME_GLAZED_TERRACOTTA:
            case Material.LIME_TERRACOTTA:
            case Material.MAGENTA_GLAZED_TERRACOTTA:
            case Material.MAGENTA_TERRACOTTA:
            case Material.ORANGE_GLAZED_TERRACOTTA:
            case Material.ORANGE_TERRACOTTA:
            case Material.PINK_GLAZED_TERRACOTTA:
            case Material.PINK_TERRACOTTA:
            case Material.PURPLE_GLAZED_TERRACOTTA:
            case Material.PURPLE_TERRACOTTA:
            case Material.RED_GLAZED_TERRACOTTA:
            case Material.RED_TERRACOTTA:
            case Material.WHITE_GLAZED_TERRACOTTA:
            case Material.WHITE_TERRACOTTA:
            case Material.YELLOW_GLAZED_TERRACOTTA:
            case Material.YELLOW_TERRACOTTA:
                handleMiningDrops(blockState);
                return;

            case Material.COAL_ORE:
            case Material.DIAMOND_ORE:
            case Material.EMERALD_ORE:
            case Material.GLOWSTONE:
            case Material.LAPIS_ORE:
            case Material.PACKED_ICE:
            case Material.NETHER_QUARTZ_ORE:
            case Material.REDSTONE_ORE:
            case Material.STONE:
            case Material.PRISMARINE:
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
            case Material.COAL_ORE:
            case Material.DIAMOND_ORE:
            case Material.EMERALD_ORE:
            case Material.END_STONE:
            case Material.GLOWSTONE:
            case Material.GOLD_ORE:
            case Material.TERRACOTTA:
            case Material.IRON_ORE:
            case Material.LAPIS_ORE:
            case Material.MOSSY_COBBLESTONE:
            case Material.NETHERRACK:
            case Material.OBSIDIAN:
            case Material.PACKED_ICE:
            case Material.REDSTONE_ORE:
            case Material.SANDSTONE:
            case Material.BLACK_GLAZED_TERRACOTTA:
            case Material.BLACK_TERRACOTTA:
            case Material.BLUE_GLAZED_TERRACOTTA:
            case Material.BLUE_TERRACOTTA:
            case Material.BROWN_GLAZED_TERRACOTTA:
            case Material.BROWN_TERRACOTTA:
            case Material.CYAN_GLAZED_TERRACOTTA:
            case Material.CYAN_TERRACOTTA:
            case Material.GRAY_GLAZED_TERRACOTTA:
            case Material.GRAY_TERRACOTTA:
            case Material.GREEN_GLAZED_TERRACOTTA:
            case Material.GREEN_TERRACOTTA:
            case Material.LIGHT_BLUE_GLAZED_TERRACOTTA:
            case Material.LIGHT_BLUE_TERRACOTTA:
            case Material.LIGHT_GRAY_GLAZED_TERRACOTTA:
            case Material.LIGHT_GRAY_TERRACOTTA:
            case Material.LIME_GLAZED_TERRACOTTA:
            case Material.LIME_TERRACOTTA:
            case Material.MAGENTA_GLAZED_TERRACOTTA:
            case Material.MAGENTA_TERRACOTTA:
            case Material.ORANGE_GLAZED_TERRACOTTA:
            case Material.ORANGE_TERRACOTTA:
            case Material.PINK_GLAZED_TERRACOTTA:
            case Material.PINK_TERRACOTTA:
            case Material.PURPLE_GLAZED_TERRACOTTA:
            case Material.PURPLE_TERRACOTTA:
            case Material.RED_GLAZED_TERRACOTTA:
            case Material.RED_TERRACOTTA:
            case Material.WHITE_GLAZED_TERRACOTTA:
            case Material.WHITE_TERRACOTTA:
            case Material.YELLOW_GLAZED_TERRACOTTA:
            case Material.YELLOW_TERRACOTTA:
            case Material.STONE:
            case Material.NETHER_QUARTZ_ORE:
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
