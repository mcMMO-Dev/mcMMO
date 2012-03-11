package com.gmail.nossr50.runnables;

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
                block.setData((byte) 0x4);
            }
            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 400) {
                block.setData((byte) 0x3);
            }
            else if (PP.getSkillLevel(SkillType.HERBALISM) >= 200) {
                block.setData((byte) 0x2);
            }
            else {
                block.setData((byte) 0x1);
            }
        }
        else {
            block.setData((byte) 0x4);
        }
    }
}
