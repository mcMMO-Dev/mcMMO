package com.gmail.nossr50.datatypes.meta;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ProjectileOriginMeta extends FixedMetadataValue {
    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param value        the value assigned to this metadata value
     */
    public ProjectileOriginMeta(@NotNull Plugin owningPlugin, int value) {
        super(owningPlugin, value);
    }
}
