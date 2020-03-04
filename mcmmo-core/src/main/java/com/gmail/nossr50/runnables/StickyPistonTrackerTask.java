package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.function.Consumer;

public class StickyPistonTrackerTask implements Consumer<Task> {
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
    public void accept(Task task) {
        if (!pluginRef.getPlaceStore().isTrue(movedBlock.getRelative(direction))) {
            return;
        }

        if (!pluginRef.getBlockTools().isPistonPiece(movedBlock.getState())) {
            // The block didn't move
            return;
        }

        // The sticky piston actually pulled the block so move the PlaceStore data
        pluginRef.getPlaceStore().setFalse(movedBlock.getRelative(direction));
        pluginRef.getPlaceStore().setTrue(movedBlock);
    }
}
