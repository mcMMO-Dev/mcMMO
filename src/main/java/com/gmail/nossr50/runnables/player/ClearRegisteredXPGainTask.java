package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearRegisteredXPGainTask extends BukkitRunnable {
    @Override
    public void run() {
        for (McMMOPlayer mmoPlayer : mcMMO.getUserManager().getPlayers()) {
            mmoPlayer.purgeExpiredXpGains();
        }
    }
}
