package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;

public final class Acrobatics {
    public static double rollThreshold         = AdvancedConfig.getInstance().getRollDamageThreshold();
    public static double gracefulRollThreshold = AdvancedConfig.getInstance().getGracefulRollDamageThreshold();
    public static double dodgeDamageModifier   = AdvancedConfig.getInstance().getDodgeDamageModifier();

    public static int dodgeXpModifier = ExperienceConfig.getInstance().getDodgeXPModifier();
    public static int rollXpModifier  = ExperienceConfig.getInstance().getRollXPModifier();
    public static int fallXpModifier  = ExperienceConfig.getInstance().getFallXPModifier();

    public static double featherFallXPModifier = ExperienceConfig.getInstance().getFeatherFallXPModifier();

    public static boolean dodgeLightningDisabled = Config.getInstance().getDodgeLightningDisabled();

    private Acrobatics() {};

    /**
     * Calculates how much damage should be dealt when Dodging
     *
     * @param damage         the base damage
     * @param damageModifier the damage modifier
     *
     * @return modified damage
     */
    protected static double calculateModifiedDodgeDamage(double damage, double damageModifier) {
        return Math.max(damage / damageModifier, 1.0);
    }

    /**
     * Calculates how much damage should be dealt when Rolling
     *
     * @param damage         the base damage
     * @param damageThreshold the damage threshold
     *
     * @return modified damage
     */
    protected static double calculateModifiedRollDamage(double damage, double damageThreshold) {
        return Math.max(damage - damageThreshold, 0.0);
    }
}
