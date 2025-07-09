package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.CancellableRunnable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class StickyPistonTrackerTask extends CancellableRunnable {
    private final BlockFace direction;
    private final Block block;
    private final Block movedBlock;

    public StickyPistonTrackerTask(BlockFace direction, Block block, Block movedBlock) {
        this.direction = direction;
        this.block = block;
        this.movedBlock = movedBlock;
    }

    @Override
    public void run() {
        if (!mcMMO.getUserBlockTracker().isIneligible(movedBlock.getRelative(direction))) {
            return;
        }

        if (!BlockUtils.isPistonPiece(movedBlock.getState())) {
            // The block didn't move
            return;
        }

        // The sticky piston actually pulled the block so move the PlaceStore data
        mcMMO.getUserBlockTracker().setEligible(movedBlock.getRelative(direction));
        BlockUtils.setUnnaturalBlock(movedBlock);
    }
}
