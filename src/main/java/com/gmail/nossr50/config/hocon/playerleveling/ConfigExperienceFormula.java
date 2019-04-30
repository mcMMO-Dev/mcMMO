package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperienceFormula {

    public static final boolean CUMULATIVE_CURVE_DEFAULT = false;

    @Setting(value = "Player-XP-Formula-Type", comment = "Determines which formula is used to determine XP needed to level" +
            "\nDefault value: LINEAR")
    private FormulaType formulaType = FormulaType.LINEAR;

    @Setting(value = "Linear-Formula-Settings", comment = "These settings are only used if you have your formula type set to Linear" +
            "LINEAR Formula: base + (level * multiplier)")
    private ConfigExperienceFormulaLinear configExperienceFormulaLinear = new ConfigExperienceFormulaLinear();

    @Setting(value = "Exponential-Formula-Settings", comment = "These settings are only used if you have your formula type set to Exponential" +
            "\nEXPONENTIAL Formula: multiplier * level ^ exponent + base")
    private ConfigExperienceFormulaExponential configExperienceFormulaExponential = new ConfigExperienceFormulaExponential();

    @Setting(value = "Use-Cumulative-XP-Curve", comment = "Replaces the value for level used in the XP formulas with a players power level." +
            "\nEffectively this makes it much harder to level, especially on exponential curve." +
            "\nDefault value: " + CUMULATIVE_CURVE_DEFAULT)
    private boolean cumulativeCurveEnabled = CUMULATIVE_CURVE_DEFAULT;

    public FormulaType getFormulaType() {
        return formulaType;
    }

    public ConfigExperienceFormulaLinear getConfigExperienceFormulaLinear() {
        return configExperienceFormulaLinear;
    }

    public boolean isCumulativeCurveEnabled() {
        return cumulativeCurveEnabled;
    }

    public ConfigExperienceFormulaExponential getConfigExperienceFormulaExponential() {
        return configExperienceFormulaExponential;
    }

    public double getMultiplier(FormulaType formulaType) {
        switch (formulaType) {
            case LINEAR:
                return getLinearMultiplier();
            case EXPONENTIAL:
                return getExponentialMultiplier();
            default:
                throw new IncorrectFormulaException(formulaType);
        }
    }

    public int getBase(FormulaType formulaType) {
        switch (formulaType) {
            case LINEAR:
                return getLinearBaseModifier();
            case EXPONENTIAL:
                return getExponentialBaseModifier();
            default:
                throw new IncorrectFormulaException(formulaType);
        }
    }

    public int getExponentialBaseModifier() {
        return configExperienceFormulaExponential.getExponentialBaseModifier();
    }

    public double getExponentialMultiplier() {
        return configExperienceFormulaExponential.getExponentialMultiplier();
    }

    public double getExponentialExponent() {
        return configExperienceFormulaExponential.getExponentialExponent();
    }

    public int getLinearBaseModifier() {
        return configExperienceFormulaLinear.getLinearBaseModifier();
    }

    public double getLinearMultiplier() {
        return configExperienceFormulaLinear.getLinearMultiplier();
    }
}