package com.gmail.nossr50.core.runnables.player;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearRegisteredXPGainTask extends BukkitRunnable {
    @Override
    public void run() {
        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
            mcMMOPlayer.getProfile().purgeExpiredXpGains();
        }
    }
}
