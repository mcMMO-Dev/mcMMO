package com.gmail.nossr50.skills.taming;

import java.util.Random;

import com.gmail.nossr50.config.AdvancedConfig;

public class Taming {
	static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
	
    public static final int ENVIRONMENTALLY_AWARE_ACTIVATION_LEVEL = advancedConfig.getEnviromentallyAwareUnlock();

    public static final int FAST_FOOD_SERVICE_ACTIVATION_CHANCE = advancedConfig.getFastFoodChance();
    public static final int FAST_FOOD_SERVICE_ACTIVATION_LEVEL = advancedConfig.getFastFoodUnlock();

    public static final int GORE_BLEED_TICKS = advancedConfig.getGoreBleedTicks();
    public static final int GORE_MAX_BONUS_LEVEL = advancedConfig.getGoreMaxBonusLevel();
    public static final int GORE_MULTIPLIER = advancedConfig.getGoreModifier();

    public static final int SHARPENED_CLAWS_ACTIVATION_LEVEL = advancedConfig.getSharpenedClawsUnlock();
    public static final int SHARPENED_CLAWS_BONUS = advancedConfig.getSharpenedClawsBonus();

    public static final int SHOCK_PROOF_ACTIVATION_LEVEL = advancedConfig.getShockProofUnlock();
    public static final int SHOCK_PROOF_MODIFIER = advancedConfig.getShockProofModifier();

    public static final int THICK_FUR_ACTIVATION_LEVEL = advancedConfig.getThickFurUnlock();
    public static final int THICK_FUR_MODIFIER = advancedConfig.getThickFurModifier();

    private static Random random = new Random();

    public static Random getRandom() {
        return random;
    }
}
