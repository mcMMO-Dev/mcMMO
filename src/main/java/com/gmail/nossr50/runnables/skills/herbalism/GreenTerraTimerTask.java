package com.gmail.nossr50.runnables.skills.herbalism;

import org.bukkit.CropState;
import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

public class GreenTerraTimerTask implements Runnable {
    private BlockState blockState;

    /**
     * Convert plants affected by the Green Terra ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public GreenTerraTimerTask(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void run() {
        switch (blockState.getType()) {
            case CROPS:
            case CARROT:
            case POTATO:
                blockState.setRawData(CropState.MEDIUM.getData());
                blockState.update(true);
                return;

            case NETHER_WARTS:
                blockState.setRawData((byte) 0x2);
                blockState.update(true);
                return;

            case COCOA:
                CocoaPlant plant = (CocoaPlant) blockState.getData();
                plant.setSize(CocoaPlantSize.MEDIUM);
                blockState.setData(plant);
                blockState.update(true);
                return;

            default:
                return;
        }
    }
}
