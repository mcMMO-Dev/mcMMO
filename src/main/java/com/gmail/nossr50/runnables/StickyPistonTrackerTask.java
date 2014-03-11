package com.gmail.nossr50.runnables;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;

public class StickyPistonTrackerTask extends BukkitRunnable {
    private BlockFace direction;
    private Block block;
    private Block movedBlock;

    public StickyPistonTrackerTask(BlockFace direction, Block block, Block movedBlock) {
        this.direction = direction;
        this.block = block;
        this.movedBlock = movedBlock;
    }

    @Override
    public void run() {
        if (!BlockUtils.shouldBeWatched(movedBlock.getState()) || movedBlock.getPistonMoveReaction() != PistonMoveReaction.MOVE || !mcMMO.getPlaceStore().isTrue(movedBlock)) {
            return;
        }

        mcMMO.getPlaceStore().setFalse(movedBlock);
        mcMMO.getPlaceStore().setTrue(block.getRelative(direction));
    }
}
