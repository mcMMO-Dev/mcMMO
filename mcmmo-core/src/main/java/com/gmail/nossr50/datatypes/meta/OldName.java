package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.mcMMO;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * This class is for storing mob names since we switch them to heart values
 */
public class OldName extends FixedMetadataValue {

    public OldName(String oldName, mcMMO plugin) {
        super((Plugin) plugin.getPlatformProvider(), oldName);
    }

}
