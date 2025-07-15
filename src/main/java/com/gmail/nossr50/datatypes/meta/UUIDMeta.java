package com.gmail.nossr50.datatypes.meta;

import java.util.UUID;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UUIDMeta extends FixedMetadataValue {
    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param value the value assigned to this metadata value
     */
    public UUIDMeta(@NotNull Plugin owningPlugin, @Nullable UUID value) {
        super(owningPlugin, value);
    }
}
