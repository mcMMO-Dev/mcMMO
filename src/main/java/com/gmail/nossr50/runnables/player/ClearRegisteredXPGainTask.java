package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearRegisteredXPGainTask extends BukkitRunnable {

    private final mcMMO pluginRef;

    public ClearRegisteredXPGainTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public void run() {
        for (McMMOPlayer mcMMOPlayer : pluginRef.getUserManager().getPlayers()) {
            mcMMOPlayer.getProfile().purgeExpiredXpGains();
        }
    }
}
