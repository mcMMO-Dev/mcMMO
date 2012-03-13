package com.gmail.nossr50.runnables;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class GreenThumbTimer implements Runnable {
    private Block block;
    private PlayerProfile PP;

    public GreenThumbTimer(Block block, PlayerProfile PP) {
        this.block = block;
        this.PP = PP;
    }

    @Override
    public void run() {
        block.setType(Material.CROPS);

        //This replants the wheat at a certain stage in development based on Herbalism Skill
        if (!PP.getGreenTerraMode()) {
            if (PP.getSkillLevel(SkillType.HERBALISM) >= 600) {
                block.setData(CropState.MEDIUM.getData());
            }
            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 400) {
                block.setData(CropState.SMALL.getData());
            }
            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 200) {
                block.setData(CropState.VERY_SMALL.getData());
            }
            else {
                block.setData(CropState.GERMINATED.getData());
            }
        }
        else {
            block.setData(CropState.MEDIUM.getData());
        }
    }
}
