package com.gmail.nossr50.skills.herbalism;

import org.bukkit.block.BlockState;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

/**
 * Convert plants affected by the Green Thumb ability.
 *
 * @param blockState The {@link BlockState} to check ability activation for
 * @param skillLevel The player's Herbalism skill level
 */
public class GreenThumbTimer implements Runnable {
    private BlockState blockState;
    private int skillLevel;

    public GreenThumbTimer(BlockState blockState, int skillLevel) {
        this.blockState = blockState;
        this.skillLevel = skillLevel;
    }

    @Override
    public void run() {
        int greenThumbStage = Math.min(Math.min(skillLevel, Herbalism.greenThumbStageMaxLevel) / Herbalism.greenThumbStageChangeLevel, 4);

        switch(blockState.getType()) {
        case CROPS:
        case CARROT:
        case POTATO:
            blockState.setRawData((byte) greenThumbStage);
            blockState.update(true);
            return;

        case NETHER_WARTS:
            if (greenThumbStage > 2) {
                blockState.setRawData((byte) 0x2);
            }
            else if (greenThumbStage == 2) {
                blockState.setRawData((byte) 0x1);
            }
            else {
                blockState.setRawData((byte) 0x0);
            }
            blockState.update(true);
            return;

        case COCOA:
            CocoaPlant plant = (CocoaPlant) blockState.getData();

            if (greenThumbStage > 1) {
                plant.setSize(CocoaPlantSize.MEDIUM);
            }
            else {
                plant.setSize(CocoaPlantSize.SMALL);
            }
            blockState.setData(plant);
            blockState.update(true);
            return;

        default:
            return;
        }
    }
}
