package com.gmail.nossr50.runnables;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.util.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PistonTrackerTask extends BukkitRunnable {
    private List<Block> blocks;
    private BlockFace direction;
    private Block futureEmptyBlock;

    public PistonTrackerTask(List<Block> blocks, BlockFace direction, Block futureEmptyBlock) {
        this.blocks = blocks;
        this.direction = direction;
        this.futureEmptyBlock = futureEmptyBlock;
    }

    @Override
    public void run() {
        // Check to see if futureEmptyBlock is empty - if it isn't; the blocks didn't move
        if (!BlockUtils.isPistonPiece(futureEmptyBlock.getState())) {
            return;
        }

        if (pluginRef.getPlaceStore().isTrue(futureEmptyBlock)) {
            pluginRef.getPlaceStore().setFalse(futureEmptyBlock);
        }

        for (Block b : blocks) {
            Block nextBlock = b.getRelative(direction);

            if (nextBlock.hasMetadata(MetadataConstants.PISTON_TRACKING_METAKEY)) {
                pluginRef.getPlaceStore().setTrue(nextBlock);
                nextBlock.removeMetadata(MetadataConstants.PISTON_TRACKING_METAKEY, pluginRef);
            } else if (pluginRef.getPlaceStore().isTrue(nextBlock)) {
                // Block doesn't have metadatakey but isTrue - set it to false
                pluginRef.getPlaceStore().setFalse(nextBlock);
            }
        }
    }
}
