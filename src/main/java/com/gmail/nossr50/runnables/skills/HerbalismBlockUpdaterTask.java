package com.gmail.nossr50.runnables.skills;

import org.bukkit.block.BlockState;

public class HerbalismBlockUpdaterTask implements Runnable {
    private BlockState blockState;

    public HerbalismBlockUpdaterTask(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void run() {
        blockState.update(true);
    }
}
