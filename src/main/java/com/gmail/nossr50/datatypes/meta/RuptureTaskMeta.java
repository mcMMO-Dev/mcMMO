package com.gmail.nossr50.datatypes.meta;

import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.runnables.skills.RuptureTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * No longer used. mcMMO stopped attaching rupture state to entities as Bukkit metadata
 * in 2.3.001; active ruptures are tracked internally instead. Use
 * {@link AbilityAPI#isBleeding(LivingEntity)} to check for an active bleed, or
 * {@link RuptureTask#getActive(Entity)} to retrieve the task.
 *
 * @deprecated mcMMO no longer creates or reads this metadata.
 */
@Deprecated(forRemoval = true, since = "2.3.001")
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
