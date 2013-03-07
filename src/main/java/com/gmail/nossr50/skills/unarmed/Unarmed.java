package com.gmail.nossr50.skills.unarmed;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Unarmed {
    public static int ironArmMaxBonusDamage = AdvancedConfig.getInstance().getIronArmMaxBonus();
    public static int ironArmIncreaseLevel  = AdvancedConfig.getInstance().getIronArmIncreaseLevel();

    public static int    disarmMaxBonusLevel = AdvancedConfig.getInstance().getDisarmMaxBonusLevel();
    public static double disarmMaxChance     = AdvancedConfig.getInstance().getDisarmChanceMax();

    public static int    deflectMaxBonusLevel = AdvancedConfig.getInstance().getDeflectMaxBonusLevel();
    public static double deflectMaxChance     = AdvancedConfig.getInstance().getDeflectChanceMax();

    public static int    ironGripMaxBonusLevel = AdvancedConfig.getInstance().getIronGripMaxBonusLevel();
    public static double ironGripMaxChance     = AdvancedConfig.getInstance().getIronGripChanceMax();

    public static boolean blockCrackerSmoothBrick = Config.getInstance().getUnarmedBlockCrackerSmoothbrickToCracked();

    public static double berserkDamageModifier = 1.5;
}
