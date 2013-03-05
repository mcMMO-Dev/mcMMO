package com.gmail.nossr50.runnables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gmail.nossr50.mcMMO;

public class StickyPistonTrackerTask implements Runnable {
    BlockPistonRetractEvent event;

    public StickyPistonTrackerTask(BlockPistonRetractEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        Block newBlock = event.getBlock().getRelative(event.getDirection());
        Block originalBlock = newBlock.getRelative(event.getDirection());

        if (originalBlock.getType() != Material.AIR) {
            return;
        }

        if (!mcMMO.placeStore.isTrue(originalBlock)) {
            return;
        }

        mcMMO.placeStore.setFalse(originalBlock);
        mcMMO.placeStore.setTrue(newBlock);
    }
}
