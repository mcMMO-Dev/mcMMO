package com.gmail.nossr50.skills.unarmed;

import com.gmail.nossr50.config.AdvancedConfig;

public class Unarmed {
    public static int ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int ironArmIncreaseLevel = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static double deflectMaxChance = AdvancedConfig.getInstance().getDisarmChanceMax() ;
    public static int deflectMaxBonusLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();

    public static double disarmMaxChance = AdvancedConfig.getInstance().getDeflectChanceMax();
    public static int disarmMaxBonusLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();

    public static double ironGripMaxChance = AdvancedConfig.getInstance().getIronGripChanceMax();
    public static int ironGripMaxBonusLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();
}