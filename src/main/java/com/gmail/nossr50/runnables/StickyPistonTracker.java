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
        Block newBlock = event.getBlock().getRelative(event.getDirection());
        Block originalBlock = newBlock.getRelative(event.getDirection());

        if (originalBlock.getType() != Material.AIR) {
            return;
        }

        if (!mcMMO.p.isPlaced(originalBlock)) {
            return;
        }

        mcMMO.p.setNotPlaced(originalBlock);
        mcMMO.p.setIsPlaced(newBlock);
    }
}
