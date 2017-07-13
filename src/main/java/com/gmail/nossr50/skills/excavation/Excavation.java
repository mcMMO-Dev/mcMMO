package com.gmail.nossr50.skills.excavation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockState;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.util.StringUtils;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(BlockState blockState) {
        String friendly = StringUtils.getFriendlyConfigMaterialDataString(blockState.getData());
        if (TreasureConfig.getInstance().excavationMap.containsKey(friendly))
            return TreasureConfig.getInstance().excavationMap.get(friendly);
        return new ArrayList<ExcavationTreasure>();
    }

    protected static int getBlockXP(BlockState blockState) {
        int xp = ExperienceConfig.getInstance().getXp(SkillType.EXCAVATION, blockState.getData());

        if (xp == 0 && mcMMO.getModManager().isCustomExcavationBlock(blockState)) {
            xp = mcMMO.getModManager().getBlock(blockState).getXpGain();
        }

        return xp;
    }
}
