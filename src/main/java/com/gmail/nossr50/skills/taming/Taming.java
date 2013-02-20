package com.gmail.nossr50.skills.taming;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Taming {
    public static int environmentallyAwareUnlockLevel = AdvancedConfig.getInstance().getEnviromentallyAwareUnlock();

    public static double fastFoodServiceActivationChance = AdvancedConfig.getInstance().getFastFoodChance();
    public static int fastFoodServiceUnlockLevel = AdvancedConfig.getInstance().getFastFoodUnlock();

    public static double goreMaxChance = AdvancedConfig.getInstance().getGoreChanceMax();
    public static int goreBleedTicks = AdvancedConfig.getInstance().getGoreBleedTicks();
    public static int goreMaxBonusLevel = AdvancedConfig.getInstance().getGoreMaxBonusLevel();
    public static int goreModifier = AdvancedConfig.getInstance().getGoreModifier();

    public static int sharpenedClawsUnlockLevel = AdvancedConfig.getInstance().getSharpenedClawsUnlock();
    public static int sharpenedClawsBonusDamage = AdvancedConfig.getInstance().getSharpenedClawsBonus();

    public static int shockProofUnlockLevel = AdvancedConfig.getInstance().getShockProofUnlock();
    public static int shockProofModifier = AdvancedConfig.getInstance().getShockProofModifier();

    public static int thickFurUnlockLevel = AdvancedConfig.getInstance().getThickFurUnlock();
    public static int thickFurModifier = AdvancedConfig.getInstance().getThickFurModifier();

    public static int wolfXp = Config.getInstance().getTamingXPWolf();
    public static int ocelotXp = Config.getInstance().getTamingXPOcelot();
}
