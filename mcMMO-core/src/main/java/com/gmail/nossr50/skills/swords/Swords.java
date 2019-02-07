package com.gmail.nossr50.skills.swords;

import com.gmail.nossr50.config.AdvancedConfig;

public class Swords {
    public static int    bleedMaxTicks      = AdvancedConfig.getInstance().getRuptureMaxTicks();
    public static int    bleedBaseTicks     = AdvancedConfig.getInstance().getRuptureBaseTicks();

    public static double  counterAttackModifier      = AdvancedConfig.getInstance().getCounterModifier();

    public static double serratedStrikesModifier   = AdvancedConfig.getInstance().getSerratedStrikesModifier();
    public static int    serratedStrikesBleedTicks = AdvancedConfig.getInstance().getSerratedStrikesTicks();
}
