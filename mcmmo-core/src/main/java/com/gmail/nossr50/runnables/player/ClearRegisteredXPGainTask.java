package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;

public class ClearRegisteredXPGainTask implements Consumer<Task> {

    private final mcMMO pluginRef;

    public ClearRegisteredXPGainTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public void accept(Task task) {
        for (BukkitMMOPlayer mcMMOPlayer : pluginRef.getUserManager().getPlayers()) {
            mcMMOPlayer.getProfile().purgeExpiredXpGains();
        }
    }
}
