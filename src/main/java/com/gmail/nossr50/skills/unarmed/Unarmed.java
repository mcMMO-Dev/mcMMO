package com.gmail.nossr50.skills.unarmed;

import com.gmail.nossr50.config.AdvancedConfig;

public class Unarmed {
    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    public static final int BONUS_DAMAGE_MAX_BONUS_MODIFIER = advancedConfig.getIronArmBonus();
    public static final int BONUS_DAMAGE_INCREASE_LEVEL = advancedConfig.getIronArmIncreaseLevel();

    public static final int DEFLECT_MAX_CHANCE = advancedConfig.getDisarmChanceMax() ;
    public static final int DEFLECT_MAX_BONUS_LEVEL = advancedConfig.getDisarmMaxBonusLevel();

    public static final int DISARM_MAX_CHANCE = advancedConfig.getDeflectChanceMax();
    public static final int DISARM_MAX_BONUS_LEVEL = advancedConfig.getDeflectMaxBonusLevel();

    public static final int IRON_GRIP_MAX_CHANCE = advancedConfig.getIronGripChanceMax();
    public static final int IRON_GRIP_MAX_BONUS_LEVEL = advancedConfig.getIronGripMaxBonusLevel(); 
}