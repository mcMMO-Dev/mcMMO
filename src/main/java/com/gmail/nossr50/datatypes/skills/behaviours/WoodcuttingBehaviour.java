package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.mcMMO;
import org.bukkit.block.BlockState;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class WoodcuttingBehaviour {

    private final mcMMO pluginRef;

    /**
     * The x/y differences to the blocks in a flat cylinder around the center
     * block, which is excluded.
     */
    private final int[][] directions = {
            new int[]{-2, -1}, new int[]{-2, 0}, new int[]{-2, 1},
            new int[]{-1, -2}, new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1}, new int[]{-1, 2},
            new int[]{0, -2}, new int[]{0, -1}, new int[]{0, 1}, new int[]{0, 2},
            new int[]{1, -2}, new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}, new int[]{1, 2},
            new int[]{2, -1}, new int[]{2, 0}, new int[]{2, 1},
    };

    public WoodcuttingBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

    }

    public int[][] getDirections() {
        return directions;
    }

    /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @return Amount of experience
     */
    public int getExperienceFromLog(BlockState blockState) {
        return pluginRef.getDynamicSettingsManager().getExperienceManager().getWoodcuttingXp(blockState.getType());
    }

    /**
     * Retrieves the experience reward from logging via Tree Feller
     * Experience is reduced per log processed so far
     * Experience is only reduced if the config option to reduce Tree Feller XP is set
     * Experience per log will not fall below 1 unless the experience for that log is set to 0 in the config
     *
     * @param blockState Log being broken
     * @param woodCount how many logs have given out XP for this tree feller so far
     * @return Amount of experience
     */
    public int processTreeFellerXPGains(BlockState blockState, int woodCount) {
        int rawXP = pluginRef.getDynamicSettingsManager().getExperienceManager().getWoodcuttingXp(blockState.getType());

        if(rawXP <= 0)
            return 0;

        if(pluginRef.getConfigManager().getConfigExperience().getExperienceWoodcutting().isReduceTreeFellerXP()) {
            int reducedXP = 1 + (woodCount * 5);
            rawXP = Math.max(1, rawXP - reducedXP);
            return rawXP;
        } else {
            return pluginRef.getDynamicSettingsManager().getExperienceManager().getWoodcuttingXp(blockState.getType());
        }
    }

    /**
     * Checks for double drops
     *
     * @param blockState Block being broken
     */
    public void checkForDoubleDrop(BlockState blockState) {
        /*if (mcMMO.getModManager().isCustomLog(blockState) && mcMMO.getModManager().getBlock(blockState).isDoubleDropEnabled()) {
            Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
        }
        else {*/
        if (pluginRef.getDynamicSettingsManager().getBonusDropManager().isBonusDropWhitelisted(blockState.getType())) {
            pluginRef.getMiscTools().dropItems(pluginRef.getMiscTools().getBlockCenter(blockState), blockState.getBlock().getDrops());
        }
        //}
    }



}
