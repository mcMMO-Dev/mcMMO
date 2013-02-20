package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Acrobatics {
    public static double dodgeMaxChance = AdvancedConfig.getInstance().getDodgeChanceMax();
    public static int dodgeMaxBonusLevel = AdvancedConfig.getInstance().getDodgeMaxBonusLevel();
    public static int dodgeXpModifier = AdvancedConfig.getInstance().getDodgeXPModifier();

    public static double rollMaxChance = AdvancedConfig.getInstance().getRollChanceMax();
    public static int rollMaxBonusLevel = AdvancedConfig.getInstance().getRollMaxBonusLevel();

    public static double gracefulRollMaxChance = AdvancedConfig.getInstance().getGracefulRollChanceMax();
    public static int gracefulRollMaxBonusLevel = AdvancedConfig.getInstance().getGracefulRollMaxBonusLevel();

    public static int rollXpModifier = AdvancedConfig.getInstance().getRollXPModifier();
    public static int fallXpModifier = AdvancedConfig.getInstance().getFallXPModifier();

    public static boolean afkLevelingDisabled = Config.getInstance().getAcrobaticsAFKDisabled();
    public static boolean dodgeLightningDisabled = Config.getInstance().getDodgeLightningDisabled();
}