package com.gmail.nossr50.events.skills.fishing;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.fishing.FishingManager;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerMasterAnglerEvent extends McMMOPlayerFishingEvent {
    private int reducedMinWaitTime;
    private int reducedMaxWaitTime;
    private final FishingManager fishingManager;

    public McMMOPlayerMasterAnglerEvent(@NotNull McMMOPlayer mcMMOPlayer,
                                        int reducedMinWaitTime,
                                        int reducedMaxWaitTime,
                                        FishingManager fishingManager) {
        super(mcMMOPlayer);
        this.fishingManager = fishingManager;
        this.reducedMinWaitTime = Math.max(reducedMinWaitTime, getReducedMinWaitTimeLowerBound());
        this.reducedMaxWaitTime = Math.max(reducedMaxWaitTime, getReducedMaxWaitTimeLowerBound());
    }

    public int getReducedMinWaitTime() {
        return reducedMinWaitTime;
    }

    public void setReducedMinWaitTime(int reducedMinWaitTime) {
        if (reducedMinWaitTime < 0 || reducedMinWaitTime > reducedMaxWaitTime) {
            throw new IllegalArgumentException("Reduced min wait time must be greater than or equal to 0" +
                    " and less than reduced max wait time.");
        }
        this.reducedMinWaitTime = Math.max(reducedMinWaitTime, getReducedMinWaitTimeLowerBound());
    }

    public int getReducedMaxWaitTime() {
        return reducedMaxWaitTime;
    }

    public void setReducedMaxWaitTime(int reducedMaxWaitTime) {
        if (reducedMaxWaitTime < 0 || reducedMaxWaitTime < reducedMinWaitTime) {
            throw new IllegalArgumentException("Reduced max wait time must be greater than or equal to 0" +
                    " and greater than reduced min wait time.");
        }
        this.reducedMaxWaitTime = Math.max(reducedMaxWaitTime, getReducedMaxWaitTimeLowerBound());
    }

    public int getReducedMinWaitTimeLowerBound() {
        return fishingManager.getMasterAnglerMinWaitLowerBound();
    }

    public int getReducedMaxWaitTimeLowerBound() {
        return fishingManager.getMasterAnglerMaxWaitLowerBound();
    }
}