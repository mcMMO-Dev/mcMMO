package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.block.BlockState;

import java.util.function.Consumer;

public class HerbalismBlockUpdaterTask implements Consumer<Task> {
    private final BlockState blockState;

    public HerbalismBlockUpdaterTask(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void accept(Task task) {
        blockState.update(true);
    }
}
