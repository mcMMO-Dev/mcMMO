package com.gmail.nossr50.skills.acrobatics;

public final class Acrobatics {
    public static double dodgeDamageModifier;
    public static int dodgeXpModifier;
//    public static boolean dodgeLightningDisabled;

    private Acrobatics() {
        dodgeDamageModifier = pluginRef.getConfigManager().getConfigAcrobatics().getDamageReductionDivisor();
        dodgeXpModifier = pluginRef.getConfigManager().getConfigExperience().getDodgeXP();
//        dodgeLightningDisabled = MainConfig.getInstance().getDodgeLightningDisabled();
    }

    protected static double calculateModifiedDodgeDamage(double damage, double damageModifier) {
        return Math.max(damage / damageModifier, 1.0);
    }
}
