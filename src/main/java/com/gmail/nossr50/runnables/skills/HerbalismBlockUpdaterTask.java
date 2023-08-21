package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.block.BlockState;

public class HerbalismBlockUpdaterTask extends CancellableRunnable {
    private final BlockState blockState;

    public HerbalismBlockUpdaterTask(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void run() {
        blockState.update(true);
    }
}
