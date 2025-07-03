package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.mcMMO;

public final class Acrobatics {
    public static double dodgeDamageModifier = mcMMO.p.getAdvancedConfig().getDodgeDamageModifier();
    public static int dodgeXpModifier = ExperienceConfig.getInstance().getDodgeXPModifier();
    public static boolean dodgeLightningDisabled = mcMMO.p.getGeneralConfig()
            .getDodgeLightningDisabled();

    private Acrobatics() {
    }

    static double calculateModifiedDodgeDamage(double damage, double damageModifier) {
        return Math.max(damage / damageModifier, 1.0);
    }
}
