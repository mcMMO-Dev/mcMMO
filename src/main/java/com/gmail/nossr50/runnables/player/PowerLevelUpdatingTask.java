package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class PowerLevelUpdatingTask extends CancellableRunnable {
    @Override
    public void run() {
        if (!ScoreboardManager.powerLevelHeartbeat()) {
            this.cancel();
        }
    }
}
