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

        if (originalBlock.getType() == Material.AIR && mcMMO.p.placeStore.isTrue(originalBlock)) {
            Block newBlock = originalBlock.getRelative(event.getDirection().getOppositeFace());

            mcMMO.p.placeStore.setFalse(originalBlock);
            mcMMO.p.placeStore.setTrue(newBlock);
        }
    }
}
