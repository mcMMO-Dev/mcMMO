package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.UpdateChecker;

/**
 * Async task
 */
public class UpdateCheckerTask implements Runnable {
    @Override
    public void run() {
        try {
            mcMMO.p.updateCheckerCallback(UpdateChecker.updateAvailable());
        }
        catch (Exception e) {
            mcMMO.p.updateCheckerCallback(false);
        }
    }
}
