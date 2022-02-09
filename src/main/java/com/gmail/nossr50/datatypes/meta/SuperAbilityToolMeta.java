package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.mcMMO;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Stores the original dig speed of a tool, also marks the tool as boosted by super abilities
 */
public class SuperAbilityToolMeta extends FixedMetadataValue {

    public SuperAbilityToolMeta(int value, mcMMO plugin) {
        super(plugin, value);
    }
}
