package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;

public class Acrobatics {
    private static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    public static final int DODGE_MAX_CHANCE = advancedConfig.getDodgeChanceMax();
    public static final int DODGE_MAX_BONUS_LEVEL = advancedConfig.getDodgeMaxBonusLevel();
    public static final int DODGE_XP_MODIFIER = advancedConfig.getDodgeXPModifier();

    public static final int ROLL_MAX_CHANCE = advancedConfig.getRollChanceMax();
    public static final int ROLL_MAX_BONUS_LEVEL = advancedConfig.getRollMaxBonusLevel();
    public static final int GRACEFUL_MAX_CHANCE = advancedConfig.getGracefulRollChanceMax();
    public static final int GRACEFUL_MAX_BONUS_LEVEL = advancedConfig.getGracefulRollMaxBonusLevel();

    public static final int ROLL_XP_MODIFIER = advancedConfig.getRollXPModifier();
    public static final int FALL_XP_MODIFIER = advancedConfig.getFallXPModifier();
}
