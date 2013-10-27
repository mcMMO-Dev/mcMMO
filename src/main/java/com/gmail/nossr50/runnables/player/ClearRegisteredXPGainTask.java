package com.gmail.nossr50.runnables.player;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

public class ClearRegisteredXPGainTask extends BukkitRunnable {
    @Override
    public void run() {
        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
            mcMMOPlayer.getProfile().purgeExpiredXpGains();
        }
    }
}
