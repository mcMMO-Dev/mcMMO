package com.gmail.nossr50.core.skills.child.smelting;

import com.gmail.nossr50.core.config.AdvancedConfig;
import com.gmail.nossr50.core.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.SubSkillType;
import com.gmail.nossr50.core.util.skills.RankUtils;

public class Smelting {

    public static int burnModifierMaxLevel = AdvancedConfig.getInstance().getBurnModifierMaxLevel();
    public static double burnTimeMultiplier = AdvancedConfig.getInstance().getBurnTimeMultiplier();
    public static int fluxMiningUnlockLevel = RankUtils.getUnlockLevel(SubSkillType.SMELTING_FLUX_MINING);
    public static double fluxMiningChance = AdvancedConfig.getInstance().getFluxMiningChance();

    public static int getRank(Player player) {
        return RankUtils.getRank(player, SubSkillType.SMELTING_UNDERSTANDING_THE_ART);
    }

    protected static int getResourceXp(ItemStack smelting) {
        return mcMMO.getModManager().isCustomOre(smelting.getType()) ? mcMMO.getModManager().getBlock(smelting.getType()).getSmeltingXpGain() : ExperienceConfig.getInstance().getXp(PrimarySkillType.SMELTING, smelting.getType());
    }
}
