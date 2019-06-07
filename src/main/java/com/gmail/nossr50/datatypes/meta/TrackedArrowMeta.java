package com.gmail.nossr50.datatypes.meta;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackedArrowMeta extends FixedMetadataValue {
    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param value        the value assigned to this metadata value
     */
    public TrackedArrowMeta(@NotNull Plugin owningPlugin, @Nullable Integer value) {
        super(owningPlugin, value);
    }
}
