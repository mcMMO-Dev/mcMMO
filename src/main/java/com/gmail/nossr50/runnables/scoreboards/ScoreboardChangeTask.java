package com.gmail.nossr50.runnables.scoreboards;

import org.bukkit.scheduler.BukkitRunnable;
import com.gmail.nossr50.util.scoreboards.ScoreboardWrapper;

public class ScoreboardChangeTask extends BukkitRunnable {
    private ScoreboardWrapper wrapper;

    public ScoreboardChangeTask(ScoreboardWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void run() {
        wrapper.tryRevertBoard();
    }
}
