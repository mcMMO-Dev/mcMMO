package com.gmail.nossr50.runnables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;

public class StickyPistonTrackerTask extends BukkitRunnable {
    private BlockFace direction;
    private Block block;

    public StickyPistonTrackerTask(BlockFace direction, Block block) {
        this.direction = direction;
        this.block = block;
    }

    @Override
    public void run() {
        Block newBlock = block.getRelative(direction);
        Block originalBlock = newBlock.getRelative(direction);

        if (originalBlock.getType() != Material.AIR || !mcMMO.getPlaceStore().isTrue(originalBlock)) {
            return;
        }

        mcMMO.getPlaceStore().setFalse(originalBlock);
        mcMMO.getPlaceStore().setTrue(newBlock);
    }
}
