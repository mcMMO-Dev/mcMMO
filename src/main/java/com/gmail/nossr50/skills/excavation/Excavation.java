package com.gmail.nossr50.skills.excavation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.skills.utilities.SkillType;

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
            return TreasuresConfig.getInstance().excavationFromDirt;

        case GRASS:
            return TreasuresConfig.getInstance().excavationFromGrass;

        case SAND:
            return TreasuresConfig.getInstance().excavationFromSand;

        case GRAVEL:
            return TreasuresConfig.getInstance().excavationFromGravel;

        case CLAY:
            return TreasuresConfig.getInstance().excavationFromClay;

        case MYCEL:
            return TreasuresConfig.getInstance().excavationFromMycel;

        case SOUL_SAND:
            return TreasuresConfig.getInstance().excavationFromSoulSand;

        default:
            return new ArrayList<ExcavationTreasure>();
        }
    }

    protected static int getBlockXP(BlockState blockState) {
        int xp = Config.getInstance().getXp(SkillType.EXCAVATION, blockState.getType());

        if (xp == 0 && ModChecks.isCustomExcavationBlock(blockState)) {
            xp = ModChecks.getCustomBlock(blockState).getXpGain();
        }

        return xp;
    }
}
