package com.gmail.nossr50.runnables;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class GreenThumbTimer implements Runnable {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private Block block;
    private PlayerProfile profile;
    private Material type;

    public GreenThumbTimer(Block block, PlayerProfile profile, Material material) {
        this.block = block;
        this.profile = profile;
        this.type = material;
    }

    @Override
    public void run() {
        if (this.block.getType() != this.type)
            this.block.setType(this.type);

        int skillLevel = this.profile.getSkillLevel(SkillType.HERBALISM);

    	final int STAGE_CHANGE = advancedConfig.getGreenThumbStageChange();

        int greenThumbStage = (int) ((double) skillLevel / (double) STAGE_CHANGE);
        if (greenThumbStage > 4) greenThumbStage = 4;

        switch(this.type) {
        case CROPS:
        case CARROT:
        case POTATO:
            //This replants the wheat at a certain stage in development based on Herbalism Skill
            if (!this.profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                if (greenThumbStage == 3) {
                    this.block.setData(CropState.MEDIUM.getData());
                }
                else if (greenThumbStage == 2) {
                    this.block.setData(CropState.SMALL.getData());
                }
                else if (greenThumbStage == 1) {
                    this.block.setData(CropState.VERY_SMALL.getData());
                }
                else {
                    this.block.setData(CropState.GERMINATED.getData());
                }
            }
            else {
                this.block.setData(CropState.MEDIUM.getData());
            }
            break;
        case NETHER_WARTS:
            if (!this.profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                if (greenThumbStage == 3) {
                    this.block.setData((byte) 2);
                }
                else if (greenThumbStage == 2) {
                    this.block.setData((byte) 1);
                }
                else {
                    this.block.setData((byte) 0);
                }
            }
            else {
                this.block.setData((byte) 2);
            }
            break;
        case COCOA:
            if (!this.profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                if (greenThumbStage == 3) {
                    this.block.setData((byte) ((this.block.getData() ^ ((byte) 0xc)) | ((byte) 4)));
                }
                else if (greenThumbStage == 2) {
                    this.block.setData((byte) ((this.block.getData() ^ ((byte) 0xc)) | ((byte) 4)));
                }
                else {
                    this.block.setData((byte) (this.block.getData() ^ ((byte) 0xc)));
                }
            }
            else {
                this.block.setData((byte) ((this.block.getData() ^ ((byte) 0xc)) | ((byte) 4)));
            }
            break;
        default:
            break;
        }
    }
}
