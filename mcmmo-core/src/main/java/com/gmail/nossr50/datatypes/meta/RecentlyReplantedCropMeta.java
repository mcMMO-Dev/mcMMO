package com.gmail.nossr50.datatypes.meta;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class RecentlyReplantedCropMeta extends FixedMetadataValue {

    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     */
    public RecentlyReplantedCropMeta(Plugin owningPlugin, Boolean recentlyPlanted) {
        super(owningPlugin, recentlyPlanted);
    }

}
