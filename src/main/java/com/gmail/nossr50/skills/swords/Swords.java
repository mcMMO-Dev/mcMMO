package com.gmail.nossr50.skills.swords;

import com.gmail.nossr50.config.AdvancedConfig;

public class Swords {
    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    public static final int BLEED_CHANCE_MAX = AdvancedConfig.getInstance().getBleedChanceMax();
    public static final int BLEED_MAX_BONUS_LEVEL = advancedConfig.getBleedMaxBonusLevel();
    public static final int MAX_BLEED_TICKS = advancedConfig.getBleedMaxTicks();
    public static final int BASE_BLEED_TICKS = advancedConfig.getBleedBaseTicks();

    public static final int COUNTER_ATTACK_CHANCE_MAX = advancedConfig.getCounterChanceMax();
    public static final int COUNTER_ATTACK_MAX_BONUS_LEVEL = advancedConfig.getCounterMaxBonusLevel();
    public static final int COUNTER_ATTACK_MODIFIER = advancedConfig.getCounterModifier();

    public static final int SERRATED_STRIKES_MODIFIER = advancedConfig.getSerratedStrikesModifier();
    public static final int SERRATED_STRIKES_BLEED_TICKS = advancedConfig.getSerratedStrikesTicks();
}
