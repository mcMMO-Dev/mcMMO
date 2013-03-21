package com.gmail.nossr50.runnables.skills;

import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

public class HerbalismBlockUpdaterTask extends BukkitRunnable {
    private BlockState blockState;

    public HerbalismBlockUpdaterTask(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void run() {
        blockState.update(true);
    }
}
