package com.gmail.nossr50.config.experience;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.config.AutoUpdateConfigLoader;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public class ExperienceConfig extends AutoUpdateConfigLoader {
    private static ExperienceConfig instance;

    private ExperienceConfig() {
        super("experienceFormula.yml");
        validate();
    }

    public static ExperienceConfig getInstance() {
        if (instance == null) {
            instance = new ExperienceConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {}

    @Override
    protected boolean validateKeys() {
        List<String> reason = new ArrayList<String>();

        if (getExpModifier() <= 0) {
            reason.add("Conversion.Exp_Modifier should be greater than 0!");
        }

        if (getMultiplier(FormulaType.EXPONENTIAL) <= 0) {
            reason.add("Experience_Formula.Exponential_Values.multiplier should be greater than 0!");
        }

        if (getMultiplier(FormulaType.LINEAR) <= 0) {
            reason.add("Experience_Formula.Linear_Values.multiplier should be greater than 0!");
        }

        if (getExponent(FormulaType.EXPONENTIAL) <= 0) {
            reason.add("Experience_Formula.Exponential_Values.exponent should be greater than 0!");
        }

        return noErrorsInConfig(reason);
    }

    /* XP Formula Multiplier */
    public FormulaType getFormulaType() { return FormulaType.getFormulaType(config.getString("Experience_Formula.Curve")); }
    public boolean getCumulativeCurveEnabled() { return config.getBoolean("Experience_Formula.Cumulative_Curve", false); }

    /* Curve values */
    public double getMultiplier(FormulaType type) { return config.getDouble("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.multiplier"); }
    public int getBase(FormulaType type) { return config.getInt("Experience_Formula." + StringUtils.getCapitalized(type.toString()) + "_Values.base"); }
    public double getExponent(FormulaType type) { return config.getDouble("Experience_Formula." + StringUtils.getCapitalized(type.toString()) +"_Values.exponent"); }

    /* Skill modifiers */
    public double getFormulaSkillModifier(SkillType skill) { return config.getDouble("Experience_Formula.Modifier." + StringUtils.getCapitalized(skill.toString())); }

    /* Conversion */
    public double getExpModifier() { return config.getDouble("Conversion.Exp_Modifier", 1); }
}
