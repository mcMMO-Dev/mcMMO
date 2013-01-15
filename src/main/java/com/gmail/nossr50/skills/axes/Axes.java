package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.config.AdvancedConfig;

public class Axes {
    public static int bonusDamageMaxBonus = AdvancedConfig.getInstance().getBonusDamageAxesBonusMax();
    public static int bonusDamageMaxBonusLevel = AdvancedConfig.getInstance().getBonusDamageAxesMaxBonusLevel();

    public static int criticalHitMaxBonusLevel = AdvancedConfig.getInstance().getAxesCriticalMaxBonusLevel();
    public static double criticalHitMaxChance = AdvancedConfig.getInstance().getAxesCriticalChance();
    public static double criticalHitPVPModifier = AdvancedConfig.getInstance().getAxesCriticalPVPModifier();
    public static double criticalHitPVEModifier = AdvancedConfig.getInstance().getAxesCriticalPVEModifier();

    public static int impactIncreaseLevel = AdvancedConfig.getInstance().getArmorImpactIncreaseLevel();
    public static double impactMaxDurabilityDamageModifier = AdvancedConfig.getInstance().getArmorImpactMaxDurabilityDamage() / 100D;

    public static double greaterImpactChance = AdvancedConfig.getInstance().getGreaterImpactChance();
    public static double greaterImpactKnockbackMultiplier = AdvancedConfig.getInstance().getGreaterImpactModifier();
    public static int greaterImpactBonusDamage = AdvancedConfig.getInstance().getGreaterImpactBonusDamage();

    public static int skullSplitterModifier = AdvancedConfig.getInstance().getSkullSplitterModifier();
}
