package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.mcMMO;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Stores how many bonus drops a block should give
 */
public class BonusDropMeta extends FixedMetadataValue {

    public BonusDropMeta(int value, mcMMO plugin) {
        super((Plugin) plugin.getPlatformProvider(), value);
    }
}