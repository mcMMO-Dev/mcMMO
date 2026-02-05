package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.UserManager;

public class ClearRegisteredXPGainTask extends CancellableRunnable {
    @Override
    public void run() {
        for (McMMOPlayer mmoPlayer : UserManager.getPlayers()) {
            mmoPlayer.getProfile().purgeExpiredXpGains();
        }
    }
}
