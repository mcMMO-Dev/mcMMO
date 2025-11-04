package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.entity.FishHook;
import org.jetbrains.annotations.NotNull;

public class MasterAnglerTask extends CancellableRunnable {
    private final @NotNull FishHook fishHook;
    private final @NotNull FishingManager fishingManager;
    private final int lureLevel;

    public MasterAnglerTask(@NotNull FishHook fishHook, @NotNull FishingManager fishingManager,
            int lureLevel) {
        this.fishHook = fishHook;
        this.fishingManager = fishingManager;
        this.lureLevel = lureLevel;
    }

    @Override
    public void run() {
        fishingManager.processMasterAngler(fishHook, lureLevel);
    }
}
