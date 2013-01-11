package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;

public class Acrobatics {
    public static int maxDodgeChance = AdvancedConfig.getInstance().getDodgeChanceMax();
    public static int maxDodgeBonusLevel = AdvancedConfig.getInstance().getDodgeMaxBonusLevel();
    public static int dodgeXpModifier = AdvancedConfig.getInstance().getDodgeXPModifier();

    public static int maxRollChance = AdvancedConfig.getInstance().getRollChanceMax();
    public static int maxRollBonusLevel = AdvancedConfig.getInstance().getRollMaxBonusLevel();
    public static int maxGracefulRollChance = AdvancedConfig.getInstance().getGracefulRollChanceMax();
    public static int maxGracefulRollBonusLevel = AdvancedConfig.getInstance().getGracefulRollMaxBonusLevel();

    public static int rollXpModifier = AdvancedConfig.getInstance().getRollXPModifier();
    public static int fallXpModifier = AdvancedConfig.getInstance().getFallXPModifier();
}
