package com.gmail.nossr50.skills.excavation;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Excavation {
    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given
     * material.
     *
     * @param material The {@link Material} to check for treasures
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(final Material material) {
        String friendly = getMaterialConfigString(material);
        if (TreasureConfig.getInstance().excavationMap.containsKey(friendly)) {
            return TreasureConfig.getInstance().excavationMap.get(friendly);
        }
        return new ArrayList<>();
    }

    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given
     * block.
     *
     * @param block The {@link Block} to check for treasures
     * @return the list of treasures that could be found
     */
    protected static List<ExcavationTreasure> getTreasures(final Block block) {
        return getTreasures(block.getType());
    }
}
