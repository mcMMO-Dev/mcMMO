package com.gmail.nossr50.runnables.scoreboards;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.util.scoreboards.ScoreboardWrapper;

public class ScoreboardQuickUpdate extends BukkitRunnable {
    final ScoreboardWrapper wrapper;

    public ScoreboardQuickUpdate(ScoreboardWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void run() {
        wrapper.updateSidebar();
    }
}
