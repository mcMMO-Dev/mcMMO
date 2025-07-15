package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.runnables.skills.RuptureTask;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RuptureTaskMeta extends FixedMetadataValue {

    private final @NotNull RuptureTask ruptureTask;

    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param ruptureTask the value assigned to this metadata value
     */
    public RuptureTaskMeta(@NotNull Plugin owningPlugin, @NotNull RuptureTask ruptureTask) {
        super(owningPlugin, ruptureTask);
        this.ruptureTask = ruptureTask;
    }

    public @NotNull RuptureTask getRuptureTimerTask() {
        return ruptureTask;
    }
}
