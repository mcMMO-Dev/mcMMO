package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.config.treasure.ExcavationTreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class ExcavationBehaviour {

    private final mcMMO pluginRef;

    public ExcavationBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    public List<ExcavationTreasure> getTreasures(BlockState blockState) {
        String friendly = StringUtils.getFriendlyConfigBlockDataString(blockState.getBlockData());
        if (ExcavationTreasureConfig.getInstance().excavationMap.containsKey(friendly))
            return ExcavationTreasureConfig.getInstance().excavationMap.get(friendly);
        return new ArrayList<>();
    }

    public int getBlockXP(BlockState blockState) {
        int xp = pluginRef.getDynamicSettingsManager().getExperienceManager().getExcavationXp(blockState.getType());

        return xp;
    }
}
