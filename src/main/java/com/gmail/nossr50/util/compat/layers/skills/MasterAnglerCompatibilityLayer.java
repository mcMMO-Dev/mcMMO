package com.gmail.nossr50.util.compat.layers.skills;

import org.bukkit.entity.FishHook;
import org.jetbrains.annotations.NotNull;

public class MasterAnglerCompatibilityLayer extends AbstractMasterAnglerCompatibility {
    @Override
    public boolean initializeLayer() {
        return true;
    }

    /**
     * Get the minimum number of ticks one has to wait for a fish biting.
     * <p>
     * The default is 100 ticks (5 seconds).<br> Note that this is before applying lure.
     *
     * @return Minimum number of ticks one has to wait for a fish biting
     */
    public int getMinWaitTime(@NotNull FishHook fishHook) {
        return fishHook.getMinWaitTime();
    }

    /**
     * Set the minimum number of ticks one has to wait for a fish biting.
     * <p>
     * The default is 100 ticks (5 seconds).<br> Note that this is before applying lure.
     *
     * @param minWaitTime Minimum number of ticks one has to wait for a fish biting
     */
    public void setMinWaitTime(@NotNull FishHook fishHook, int minWaitTime) {
        fishHook.setMinWaitTime(minWaitTime);
    }

    /**
     * Get the maximum number of ticks one has to wait for a fish biting.
     * <p>
     * The default is 600 ticks (30 seconds).<br> Note that this is before applying lure.
     *
     * @return Maximum number of ticks one has to wait for a fish biting
     */
    public int getMaxWaitTime(@NotNull FishHook fishHook) {
        return fishHook.getMaxWaitTime();
    }

    /**
     * Set the maximum number of ticks one has to wait for a fish biting.
     * <p>
     * The default is 600 ticks (30 seconds).<br> Note that this is before applying lure.
     *
     * @param maxWaitTime Maximum number of ticks one has to wait for a fish biting
     */
    public void setMaxWaitTime(@NotNull FishHook fishHook, int maxWaitTime) {
        fishHook.setMaxWaitTime(maxWaitTime);
    }

    /**
     * Get whether the lure enchantment should be applied to reduce the wait time.
     * <p>
     * The default is true.<br> Lure reduces the wait time by 100 ticks (5 seconds) for each level
     * of the enchantment.
     *
     * @return Whether the lure enchantment should be applied to reduce the wait time
     */
    public boolean getApplyLure(@NotNull FishHook fishHook) {
        return fishHook.getApplyLure();
    }

    /**
     * Set whether the lure enchantment should be applied to reduce the wait time.
     * <p>
     * The default is true.<br> Lure reduces the wait time by 100 ticks (5 seconds) for each level
     * of the enchantment.
     *
     * @param applyLure Whether the lure enchantment should be applied to reduce the wait time
     */
    public void setApplyLure(@NotNull FishHook fishHook, boolean applyLure) {
        fishHook.setApplyLure(applyLure);
    }
}
