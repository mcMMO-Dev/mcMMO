package com.gmail.nossr50.runnables;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class GreenThumbTimer implements Runnable {
    private Block block;
    private PlayerProfile profile;

    public GreenThumbTimer(Block block, PlayerProfile profile) {
        this.block = block;
        this.profile = profile;
    }

    @Override
    public void run() {
        block.setType(Material.CROPS);

        //This replants the wheat at a certain stage in development based on Herbalism Skill
        if (!profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
            if (profile.getSkillLevel(SkillType.HERBALISM) >= 600) {
                block.setData(CropState.MEDIUM.getData());
            }
            else if (profile.getSkillLevel(SkillType.HERBALISM) >= 400) {
                block.setData(CropState.SMALL.getData());
            }
            else if (profile.getSkillLevel(SkillType.HERBALISM) >= 200) {
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
