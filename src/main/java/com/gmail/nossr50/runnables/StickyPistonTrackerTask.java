package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

public class StickyPistonTrackerTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private BlockFace direction;
    private Block block;
    private Block movedBlock;

    public StickyPistonTrackerTask(mcMMO pluginRef, BlockFace direction, Block block, Block movedBlock) {
        this.pluginRef = pluginRef;
        this.direction = direction;
        this.block = block;
        this.movedBlock = movedBlock;
    }

    @Override
    public void run() {
        if (!pluginRef.getPlaceStore().isTrue(movedBlock.getRelative(direction))) {
            return;
        }

        if (!BlockUtils.isPistonPiece(movedBlock.getState())) {
            // The block didn't move
            return;
        }

        // The sticky piston actually pulled the block so move the PlaceStore data
        pluginRef.getPlaceStore().setFalse(movedBlock.getRelative(direction));
        pluginRef.getPlaceStore().setTrue(movedBlock);
    }
}
