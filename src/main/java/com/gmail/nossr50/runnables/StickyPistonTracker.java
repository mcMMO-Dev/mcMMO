package com.gmail.nossr50.runnables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gmail.nossr50.mcMMO;

public class StickyPistonTracker implements Runnable {
    BlockPistonRetractEvent event;

    public StickyPistonTracker(BlockPistonRetractEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        Block originalBlock = event.getRetractLocation().getBlock();

        if (originalBlock.getType() == Material.AIR && mcMMO.placeStore.isTrue(originalBlock)) {
            Block newBlock = originalBlock.getRelative(event.getDirection().getOppositeFace());

            mcMMO.placeStore.setFalse(originalBlock);
            mcMMO.placeStore.setTrue(newBlock);
        }
    }
}
