package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.mcMMO;

public final class Acrobatics {
    public static double dodgeDamageModifier;
    public static int dodgeXpModifier;
//    public static boolean dodgeLightningDisabled;

    private Acrobatics() {
        dodgeDamageModifier = mcMMO.getConfigManager().getConfigAcrobatics().getDamageReductionDivisor();
        dodgeXpModifier = mcMMO.getConfigManager().getExperienceConfig().getDodgeXPModifier();
//        dodgeLightningDisabled = MainConfig.getInstance().getDodgeLightningDisabled();
    }

    protected static double calculateModifiedDodgeDamage(double damage, double damageModifier) {
        return Math.max(damage / damageModifier, 1.0);
    }
}
