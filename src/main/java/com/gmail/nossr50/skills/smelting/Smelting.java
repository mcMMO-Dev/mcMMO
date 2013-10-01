package com.gmail.nossr50.skills.smelting;

import org.bukkit.Material;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class Smelting {
    // The order of the values is extremely important, a few methods depend on it to work properly
    public enum Tier {
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getSmeltingRankLevel(this);
        }

        protected int getVanillaXPBoostModifier() {
            return AdvancedConfig.getInstance().getSmeltingVanillaXPBoostMultiplier(this);
        }
    }

    public static int    burnModifierMaxLevel = AdvancedConfig.getInstance().getBurnModifierMaxLevel();
    public static double burnTimeMultiplier   = AdvancedConfig.getInstance().getBurnTimeMultiplier();

    public static int    secondSmeltMaxLevel  = AdvancedConfig.getInstance().getSecondSmeltMaxLevel();
    public static double secondSmeltMaxChance = AdvancedConfig.getInstance().getSecondSmeltMaxChance();

    public static int    fluxMiningUnlockLevel = AdvancedConfig.getInstance().getFluxMiningUnlockLevel();
    public static double fluxMiningChance      = AdvancedConfig.getInstance().getFluxMiningChance();

    protected static int getResourceXp(Material resourceType) {
        int xp = ExperienceConfig.getInstance().getXp(SkillType.SMELTING, resourceType);

        if (resourceType == Material.GLOWING_REDSTONE_ORE) {
            xp = ExperienceConfig.getInstance().getXp(SkillType.SMELTING, Material.REDSTONE_ORE);
        }

        return xp;
    }
}
