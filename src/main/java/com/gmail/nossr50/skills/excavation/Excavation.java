package com.gmail.nossr50.skills.excavation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.util.ModUtils;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(BlockState blockState) {
        switch (blockState.getType()) {
            case DIRT:
                return TreasureConfig.getInstance().excavationFromDirt;

            case GRASS:
                return TreasureConfig.getInstance().excavationFromGrass;

            case SAND:
                return TreasureConfig.getInstance().excavationFromSand;

            case GRAVEL:
                return TreasureConfig.getInstance().excavationFromGravel;

            case CLAY:
                return TreasureConfig.getInstance().excavationFromClay;

            case MYCEL:
                return TreasureConfig.getInstance().excavationFromMycel;

            case SOUL_SAND:
                return TreasureConfig.getInstance().excavationFromSoulSand;

            default:
                return new ArrayList<ExcavationTreasure>();
        }
    }

    protected static int getBlockXP(BlockState blockState) {
        int xp = Config.getInstance().getXp(SkillType.EXCAVATION, blockState.getType());

        if (xp == 0 && ModUtils.isCustomExcavationBlock(blockState)) {
            xp = ModUtils.getCustomBlock(blockState).getXpGain();
        }

        return xp;
    }
}
