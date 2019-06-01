package com.gmail.nossr50.datatypes.skills.properties;

public class AbstractDamageProperty implements DamageProperty {
    private double pveModifier;
    private double pvpModifier;

    public AbstractDamageProperty(double pveModifier, double pvpModifier) {
        this.pveModifier = pveModifier;
        this.pvpModifier = pvpModifier;
    }

    @Override
    public double getPVEModifier() {
        return pveModifier;
    }

    @Override
    public double getPVPModifier() {
        return pvpModifier;
    }
}
