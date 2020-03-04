package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;

public class PowerLevelUpdatingTask implements Consumer<Task> {

    private final mcMMO pluginRef;

    public PowerLevelUpdatingTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public void accept(Task task) {
        if (!pluginRef.getScoreboardManager().powerLevelHeartbeat()) {
            task.cancel();
        }
    }
}
