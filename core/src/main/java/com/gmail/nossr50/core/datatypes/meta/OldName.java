package com.gmail.nossr50.core.datatypes.meta;


/**
 * This class is for storing mob names since we switch them to heart values
 */
public class OldName extends FixedMetadataValue {

    public OldName(String oldName, mcMMO plugin) {
        super(plugin, oldName);
    }
}
