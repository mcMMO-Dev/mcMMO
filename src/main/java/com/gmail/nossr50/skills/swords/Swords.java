package com.gmail.nossr50.skills.swords;

import com.gmail.nossr50.config.AdvancedConfig;

public class Swords {
    public static double bleedMaxChance = AdvancedConfig.getInstance().getBleedChanceMax();
    public static int bleedMaxBonusLevel = AdvancedConfig.getInstance().getBleedMaxBonusLevel();
    public static int bleedMaxTicks = AdvancedConfig.getInstance().getBleedMaxTicks();
    public static int bleedBaseTicks = AdvancedConfig.getInstance().getBleedBaseTicks();

    public static double counterAttackMaxChance = AdvancedConfig.getInstance().getCounterChanceMax();
    public static int counterAttackMaxBonusLevel = AdvancedConfig.getInstance().getCounterMaxBonusLevel();
    public static int counterAttackModifier = AdvancedConfig.getInstance().getCounterModifier();

    public static int serratedStrikesModifier = AdvancedConfig.getInstance().getSerratedStrikesModifier();
    public static int serratedStrikesBleedTicks = AdvancedConfig.getInstance().getSerratedStrikesTicks();
}

