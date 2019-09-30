package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.mcMMO;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Stores how many bonus drops a block should give
 */
public class BonusDropMeta extends FixedMetadataValue {

    public BonusDropMeta(int value, mcMMO plugin) {
        super(plugin, value);
    }
}