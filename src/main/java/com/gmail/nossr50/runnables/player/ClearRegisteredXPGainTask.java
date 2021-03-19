package com.gmail.nossr50.runnables.player;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearRegisteredXPGainTask extends BukkitRunnable {
    @Override
    public void run() {
        for (OnlineMMOPlayer mmoPlayer : UserManager.getPlayers()) {
            mmoPlayer.purgeExpiredXpGains();
        }
    }
}
