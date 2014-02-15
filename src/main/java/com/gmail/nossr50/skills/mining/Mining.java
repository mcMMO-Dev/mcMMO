package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Misc;

public class Mining {

    /**
     * Calculate XP gain for Mining.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    protected static int getBlockXp(BlockState blockState) {
        Material blockType = blockState.getType();
        int xp = ExperienceConfig.getInstance().getXp(SkillType.MINING, blockType != Material.GLOWING_REDSTONE_ORE ? blockType : Material.REDSTONE_ORE);

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
            case ENDER_STONE:
            case GOLD_ORE:
            case HARD_CLAY:
            case IRON_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case SANDSTONE:
            case STAINED_CLAY:
                handleMiningDrops(blockState);
                return;

            case GLOWING_REDSTONE_ORE:
                if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                    Misc.dropItem(blockState.getLocation(), new ItemStack(Material.REDSTONE_ORE));
                }
                return;

            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GLOWSTONE:
            case LAPIS_ORE:
            case PACKED_ICE:
            case QUARTZ_ORE:
            case REDSTONE_ORE:
            case STONE:
                Misc.dropItem(blockState.getLocation(), new ItemStack(blockType));
                return;

            default:
                if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItem(blockState.getLocation(), blockState.getData().toItemStack(1));
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
            case ENDER_STONE:
            case GLOWSTONE:
            case GOLD_ORE:
            case HARD_CLAY:
            case IRON_ORE:
            case LAPIS_ORE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case OBSIDIAN:
            case PACKED_ICE:
            case REDSTONE_ORE:
            case SANDSTONE:
            case STAINED_CLAY:
            case STONE:
            case QUARTZ_ORE:
                Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                return;

            case GLOWING_REDSTONE_ORE:
                if (Config.getInstance().getDoubleDropsEnabled(SkillType.MINING, Material.REDSTONE_ORE)) {
                    Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                }
                return;

            default:
                if (mcMMO.getModManager().isCustomMiningBlock(blockState)) {
                    Misc.dropItems(blockState.getLocation(), blockState.getBlock().getDrops());
                }
                return;
        }
    }
}
