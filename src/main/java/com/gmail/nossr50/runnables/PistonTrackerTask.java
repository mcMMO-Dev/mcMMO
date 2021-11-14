package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.MetadataConstants;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PistonTrackerTask extends BukkitRunnable {
    private final List<Block> blocks;
    private final BlockFace direction;
    private final Block futureEmptyBlock;

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

        if (mcMMO.getPlaceStore().isTrue(futureEmptyBlock)) {
            mcMMO.getPlaceStore().setFalse(futureEmptyBlock);
        }

        for (Block b : blocks) {
            Block nextBlock = b.getRelative(direction);

            if (nextBlock.hasMetadata(MetadataConstants.METADATA_KEY_PISTON_TRACKING)) {
                mcMMO.getPlaceStore().setTrue(nextBlock);
                nextBlock.removeMetadata(MetadataConstants.METADATA_KEY_PISTON_TRACKING, mcMMO.p);
            }
            else if (mcMMO.getPlaceStore().isTrue(nextBlock)) {
                // Block doesn't have metadatakey but isTrue - set it to false
                mcMMO.getPlaceStore().setFalse(nextBlock);
            }
        }
    }
}
