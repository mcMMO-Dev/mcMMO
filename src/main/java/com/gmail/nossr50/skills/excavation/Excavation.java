package com.gmail.nossr50.skills.excavation;

import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param block The {@link Block} to check for treasures
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(Block block) {
        String friendly = getMaterialConfigString(block.getBlockData().getMaterial());
        if (TreasureConfig.getInstance().excavationMap.containsKey(friendly))
            return TreasureConfig.getInstance().excavationMap.get(friendly);
        return new ArrayList<>();
    }
}
