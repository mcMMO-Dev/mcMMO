package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceFormula {

    private static final boolean CUMULATIVE_CURVE_DEFAULT = false;
    private static final HashMap<PrimarySkillType, Float> SKILL_FORMULA_MODIFIER_DEFAULT;

    static {
        SKILL_FORMULA_MODIFIER_DEFAULT = new HashMap<>();
        //TODO: This code is causing compiler issues
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.ACROBATICS, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.ALCHEMY, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.AXES, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.ARCHERY, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.EXCAVATION, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.FISHING, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.HERBALISM, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.MINING, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.REPAIR, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.SWORDS, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.TAMING, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.UNARMED, 1.0f);
        SKILL_FORMULA_MODIFIER_DEFAULT.put(PrimarySkillType.WOODCUTTING, 1.0f);
    }

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

    @Setting(value = "Skill-Formula-Multipliers", comment = "The end result of how much XP is needed to level is determined by multiplying against this value" +
            "\nHigher values will make skills take longer to level, lower values will decrease time to level instead.")
    private HashMap<PrimarySkillType, Float> skillXpModifier = SKILL_FORMULA_MODIFIER_DEFAULT;

    public FormulaType getFormulaType() {
        return formulaType;
    }

    public float getSkillXpFormulaModifier(PrimarySkillType primarySkillType) {
        return skillXpModifier.get(primarySkillType);
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

    public float getMultiplier(FormulaType formulaType) {
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

    public float getExponentialMultiplier() {
        return configExperienceFormulaExponential.getExponentialMultiplier();
    }

    public float getExponentialExponent() {
        return configExperienceFormulaExponential.getExponentialExponent();
    }

    public int getLinearBaseModifier() {
        return configExperienceFormulaLinear.getLinearBaseModifier();
    }

    public float getLinearMultiplier() {
        return configExperienceFormulaLinear.getLinearMultiplier();
    }
}