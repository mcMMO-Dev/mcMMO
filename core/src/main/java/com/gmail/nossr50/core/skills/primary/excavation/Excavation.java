package com.gmail.nossr50.core.skills.primary.excavation;

import com.gmail.nossr50.core.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.config.treasure.TreasureConfig;
import com.gmail.nossr50.core.mcmmo.block.BlockState;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.treasure.ExcavationTreasure;
import com.gmail.nossr50.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure |ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(BlockState blockState) {
        String friendly = StringUtils.getFriendlyConfigBlockDataString(blockState.getBlockData());
        if (TreasureConfig.getInstance().excavationMap.containsKey(friendly))
            return TreasureConfig.getInstance().excavationMap.get(friendly);
        return new ArrayList<ExcavationTreasure>();
    }

    protected static int getBlockXP(BlockState blockState) {
        int xp = ExperienceConfig.getInstance().getXp(PrimarySkillType.EXCAVATION, blockState.getType());

        if (xp == 0 && mcMMO.getModManager().isCustomExcavationBlock(blockState)) {
            xp = mcMMO.getModManager().getBlock(blockState).getXpGain();
        }

        return xp;
    }
}
