package com.gmail.nossr50.skills.excavation;

import com.gmail.nossr50.config.treasure.ExcavationTreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(BlockState blockState) {
        String friendly = StringUtils.getFriendlyConfigBlockDataString(blockState.getBlockData());
        if (ExcavationTreasureConfig.getInstance().excavationMap.containsKey(friendly))
            return ExcavationTreasureConfig.getInstance().excavationMap.get(friendly);
        return new ArrayList<>();
    }

    protected static int getBlockXP(BlockState blockState) {
        int xp = mcMMO.getDynamicSettingsManager().getExperienceManager().getExcavationXp(blockState.getType());

        return xp;
    }
}
