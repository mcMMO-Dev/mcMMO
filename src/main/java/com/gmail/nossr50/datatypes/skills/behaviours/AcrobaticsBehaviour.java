package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.mcMMO;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class AcrobaticsBehaviour {
    private final mcMMO pluginRef;

    private double dodgeDamageModifier;
    private int dodgeXpModifier;

    public AcrobaticsBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        dodgeDamageModifier = pluginRef.getConfigManager().getConfigAcrobatics().getDamageReductionDivisor();
        dodgeXpModifier = pluginRef.getConfigManager().getConfigExperience().getDodgeXP();
    }

    public double calculateModifiedDodgeDamage(double damage, double damageModifier) {
        return Math.max(damage / damageModifier, 1.0);
    }

    public double getDodgeDamageModifier() {
        return dodgeDamageModifier;
    }

    public int getDodgeXpModifier() {
        return dodgeXpModifier;
    }
}
