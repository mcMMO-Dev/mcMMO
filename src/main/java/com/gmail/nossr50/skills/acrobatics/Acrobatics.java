package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public final class Acrobatics {
    public static int    dodgeMaxBonusLevel  = AdvancedConfig.getInstance().getDodgeMaxBonusLevel();
    public static int    dodgeDamageModifier = AdvancedConfig.getInstance().getDodgeDamageModifier();
    public static double dodgeMaxChance      = AdvancedConfig.getInstance().getDodgeChanceMax();

    public static int    rollMaxBonusLevel = AdvancedConfig.getInstance().getRollMaxBonusLevel();
    public static int    rollThreshold     = AdvancedConfig.getInstance().getRollDamageThreshold();
    public static double rollMaxChance     = AdvancedConfig.getInstance().getRollChanceMax();

    public static int    gracefulRollMaxBonusLevel   = AdvancedConfig.getInstance().getGracefulRollMaxBonusLevel();
    public static int    gracefulRollThreshold       = AdvancedConfig.getInstance().getGracefulRollDamageThreshold();
    public static int    gracefulRollSuccessModifier = AdvancedConfig.getInstance().getGracefulRollSuccessModifer();
    public static double gracefulRollMaxChance       = AdvancedConfig.getInstance().getGracefulRollChanceMax();

    public static int dodgeXpModifier = AdvancedConfig.getInstance().getDodgeXPModifier();
    public static int rollXpModifier  = AdvancedConfig.getInstance().getRollXPModifier();
    public static int fallXpModifier  = AdvancedConfig.getInstance().getFallXPModifier();

    public static boolean afkLevelingDisabled    = Config.getInstance().getAcrobaticsAFKDisabled();
    public static boolean dodgeLightningDisabled = Config.getInstance().getDodgeLightningDisabled();

    private Acrobatics() {};

    protected static int calculateModifiedDodgeDamage(int damage, int damageModifier) {
        return Math.max(damage / damageModifier, 1);
    }

    protected static int calculateModifiedRollDamage(int damage, int damageThreshold) {
        return Math.max(damage - damageThreshold, 0);
    }
}
