package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLeveling {

    /*
     * CONFIG NODES
     */

    @Setting(value = "Player-Level-Caps",
            comment = "Restrict players from going above certain skill levels" +
                    "\nPlayers that have skills above the limit will have their skill levels truncated down to the limit.")
    private ConfigSectionLevelCaps configSectionLevelCaps = new ConfigSectionLevelCaps();

    @Setting(value = "General", comment = "Settings for player leveling that don't fall into other categories")
    private ConfigSectionLevelingGeneral configSectionLevelingGeneral = new ConfigSectionLevelingGeneral();

    @Setting(value = "Early-Game-Boost", comment = "mcMMO incorporates an early game XP boost to get players to the first abilities in each skill faster." +
            "\nUsing default settings, players will reach level 5 (or 50 in RetroMode) much faster than they normally would.")
    private ConfigLevelEarlyGameBoost earlyGameBoost = new ConfigLevelEarlyGameBoost();

    @Setting(value = "Experience-Formula")
    private ConfigExperienceFormula configExperienceFormula = new ConfigExperienceFormula();

    /*
     * GETTER BOILERPLATE
     */

    public double getSkillXpFormulaModifier(PrimarySkillType primarySkillType) {
        return getConfigExperienceFormula().getSkillXpFormulaModifier(primarySkillType);
    }

    public boolean isCumulativeCurveEnabled() {
        return getConfigExperienceFormula().isCumulativeCurveEnabled();
    }

    public double getEarlyGameBoostMultiplier() {
        return earlyGameBoost.getEarlyGameBoostMultiplier();
    }

    public boolean isEnableEarlyGameBoost() {
        return earlyGameBoost.isEnableEarlyGameBoost();
    }

    public ConfigLevelEarlyGameBoost getEarlyGameBoost() {
        return earlyGameBoost;
    }

    public ConfigSectionLevelCaps getConfigSectionLevelCaps() {
        return configSectionLevelCaps;
    }

    public ConfigSectionLevelingGeneral getConfigSectionLevelingGeneral() {
        return configSectionLevelingGeneral;
    }

    public int getStartingLevel() {
        return configSectionLevelingGeneral.getStartingLevel();
    }

    public ConfigSectionLevelScaling getConfigSectionLevelScaling() {
        return configSectionLevelingGeneral.getConfigSectionLevelScaling();
    }

    public ConfigExperienceFormula getConfigExperienceFormula() {
        return configExperienceFormula;
    }

    public FormulaType getFormulaType() {
        return configExperienceFormula.getFormulaType();
    }

    public boolean isRetroModeEnabled() {
        return getConfigSectionLevelScaling().isRetroModeEnabled();
    }

    public ConfigExperienceFormulaLinear getConfigExperienceFormulaLinear() {
        return configExperienceFormula.getConfigExperienceFormulaLinear();
    }

    public ConfigExperienceFormulaExponential getConfigExperienceFormulaExponential() {
        return configExperienceFormula.getConfigExperienceFormulaExponential();
    }

    public int getBase(FormulaType formulaType) {
        return configExperienceFormula.getBase(formulaType);
    }

    public double getMultiplier(FormulaType formulaType) {
        return configExperienceFormula.getMultiplier(formulaType);
    }

    public int getExponentialBaseModifier() {
        return configExperienceFormula.getExponentialBaseModifier();
    }

    public double getExponentialMultiplier() {
        return configExperienceFormula.getExponentialMultiplier();
    }

    public double getExponentialExponent() {
        return configExperienceFormula.getExponentialExponent();
    }

    public int getLinearBaseModifier() {
        return configExperienceFormula.getLinearBaseModifier();
    }

    public double getLinearMultiplier() {
        return configExperienceFormula.getLinearMultiplier();
    }

    /*
     * HELPER METHODS
     */

    public int getLevelCap(PrimarySkillType primarySkillType) {
        switch (primarySkillType) {
            case ACROBATICS:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAcrobatics().getLevelCap();
            case ALCHEMY:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAlchemy().getLevelCap();
            case ARCHERY:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getArchery().getLevelCap();
            case AXES:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAxes().getLevelCap();
            case EXCAVATION:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getExcavation().getLevelCap();
            case FISHING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getFishing().getLevelCap();
            case HERBALISM:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getHerbalism().getLevelCap();
            case MINING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getMining().getLevelCap();
            case REPAIR:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getRepair().getLevelCap();
            case SWORDS:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSwords().getLevelCap();
            case TAMING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getTaming().getLevelCap();
            case UNARMED:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getUnarmed().getLevelCap();
            case WOODCUTTING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getWoodcutting().getLevelCap();
            case SMELTING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSmelting().getLevelCap();
            case SALVAGE:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSalvage().getLevelCap();
            default:
                return Integer.MAX_VALUE;
        }
    }

    public boolean isLevelCapEnabled(PrimarySkillType primarySkillType) {
        switch (primarySkillType) {
            case ACROBATICS:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAcrobatics().isLevelCapEnabled();
            case ALCHEMY:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAlchemy().isLevelCapEnabled();
            case ARCHERY:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getArchery().isLevelCapEnabled();
            case AXES:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getAxes().isLevelCapEnabled();
            case EXCAVATION:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getExcavation().isLevelCapEnabled();
            case FISHING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getFishing().isLevelCapEnabled();
            case HERBALISM:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getHerbalism().isLevelCapEnabled();
            case MINING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getMining().isLevelCapEnabled();
            case REPAIR:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getRepair().isLevelCapEnabled();
            case SWORDS:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSwords().isLevelCapEnabled();
            case TAMING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getTaming().isLevelCapEnabled();
            case UNARMED:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getUnarmed().isLevelCapEnabled();
            case WOODCUTTING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getWoodcutting().isLevelCapEnabled();
            case SMELTING:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSmelting().isLevelCapEnabled();
            case SALVAGE:
                return configSectionLevelCaps.getConfigSectionSkillLevelCaps().getSalvage().isLevelCapEnabled();
            default:
                return false;
        }
    }
}
