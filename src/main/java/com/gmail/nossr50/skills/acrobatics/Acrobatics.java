package com.gmail.nossr50.skills.acrobatics;

import java.util.Random;

import com.gmail.nossr50.config.AdvancedConfig;

public class Acrobatics {
    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    public static final int DODGE_MAX_BONUS_LEVEL = advancedConfig.getDodgeMaxBonusLevel();
    public static final int DODGE_XP_MODIFIER = advancedConfig.getDodgeXPModifier();

    public static final int FALL_XP_MODIFIER = advancedConfig.getFallXPModifier();
    public static final int ROLL_MAX_BONUS_LEVEL = advancedConfig.getRollMaxBonusLevel();
    public static final int ROLL_XP_MODIFIER = advancedConfig.getRollXPModifier();

    private static Random random = new Random();

    public static Random getRandom() {
        return random;
    }
}
