package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Smelting {

    public static int getRank(Player player)
    {
        return RankUtils.getRank(player, SubSkillType.SMELTING_UNDERSTANDING_THE_ART);
    }

    //public static int    fluxMiningUnlockLevel = RankUtils.getUnlockLevel(SubSkillType.SMELTING_FLUX_MINING);

    protected static int getResourceXp(ItemStack smelting) {
        return mcMMO.getModManager().isCustomOre(smelting.getType()) ? mcMMO.getModManager().getBlock(smelting.getType()).getSmeltingXpGain() : ExperienceConfig.getInstance().getXp(PrimarySkillType.SMELTING, smelting.getType());
    }
}
