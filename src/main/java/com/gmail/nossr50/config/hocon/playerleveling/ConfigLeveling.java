package com.gmail.nossr50.config.hocon.playerleveling;

import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.HashMap;

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

    @Setting(value = "Diminished-Returns", comment = "Penalize players for gaining XP too quickly in a given time period.")
    private ConfigLevelingDiminishedReturns configLevelingDiminishedReturns = new ConfigLevelingDiminishedReturns();

    @Setting(value = "Experience-Bars", comment = "Settings related to experience bars")
    private ConfigExperienceBars configExperienceBars = new ConfigExperienceBars();

    /*
     * GETTER BOILERPLATE
     */

    public BarColor getXPBarColor(PrimarySkillType primarySkillType) {
        return configExperienceBars.getXPBarColor(primarySkillType);
    }

    public BarStyle getXPBarStyle(PrimarySkillType primarySkillType) {
        return configExperienceBars.getXPBarStyle(primarySkillType);
    }

    public boolean isPartyExperienceTriggerXpBarDisplay() {
        return configExperienceBars.isPartyExperienceTriggerXpBarDisplay();
    }

    public boolean isPassiveGainXPBars() {
        return configExperienceBars.isPassiveGainXPBars();
    }

    public boolean isMoreDetailedXPBars() {
        return configExperienceBars.isMoreDetailedXPBars();
    }

    public boolean isEnableXPBars() {
        return configExperienceBars.isEnableXPBars();
    }

    public HashMap<PrimarySkillType, Boolean> getXpBarSpecificToggles() {
        return configExperienceBars.getXpBarSpecificToggles();
    }

    public HashMap<PrimarySkillType, BarColor> getXpBarColorMap() {
        return configExperienceBars.getXpBarColorMap();
    }

    public HashMap<PrimarySkillType, BarStyle> getXpBarStyleMap() {
        return configExperienceBars.getXpBarStyleMap();
    }

    public boolean getXPBarToggle(PrimarySkillType primarySkillType) {
        return configExperienceBars.getXPBarToggle(primarySkillType);
    }

    public ConfigExperienceBars getConfigExperienceBars() {
        return configExperienceBars;
    }

    public double getGuaranteedMinimums() {
        return configLevelingDiminishedReturns.getGuaranteedMinimums();
    }

    public boolean isDiminishedReturnsEnabled() {
        return configLevelingDiminishedReturns.isDiminishedReturnsEnabled();
    }

    public int getDimishedReturnTimeInterval() {
        return configLevelingDiminishedReturns.getDimishedReturnTimeInterval();
    }

    public HashMap<PrimarySkillType, Integer> getSkillThresholds() {
        return configLevelingDiminishedReturns.getSkillThresholds();
    }

    public int getSkillThreshold(PrimarySkillType primarySkillType) {
        return configLevelingDiminishedReturns.getSkillThreshold(primarySkillType);
    }

    public ConfigLevelingDiminishedReturns getConfigLevelingDiminishedReturns() {
        return configLevelingDiminishedReturns;
    }

    public double getSkillXpFormulaModifier(PrimarySkillType primarySkillType) {
        return getConfigExperienceFormula().getSkillXpFormulaModifier(primarySkillType);
    }

    public boolean isCumulativeCurveEnabled() {
        return getConfigExperienceFormula().isCumulativeCurveEnabled();
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

    public ConfigSectionSkillLevelCap getPowerLevelSettings() {
        return configSectionLevelCaps.getPowerLevelSettings();
    }

    public boolean getReducePlayerSkillsAboveCap() {
        return configSectionLevelCaps.getReducePlayerSkillsAboveCap();
    }

    public ConfigSectionSkillLevelCaps getConfigSectionSkillLevelCaps() {
        return configSectionLevelCaps.getConfigSectionSkillLevelCaps();
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

    public boolean isPowerLevelCapEnabled() {
        return configSectionLevelCaps.isPowerLevelCapEnabled();
    }

    public int getPowerLevelCap() {
        return configSectionLevelCaps.getPowerLevelCap();
    }

    /*
     * HELPER METHODS
     */

    public int getSkillLevelCap(PrimarySkillType primarySkillType) {
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
                mcMMO.p.getLogger().severe("No defined level cap for " + primarySkillType.toString() + " - Contact the mcMMO dev team!");
                return Integer.MAX_VALUE;
        }
    }

    public boolean isSkillLevelCapEnabled(PrimarySkillType primarySkillType) {
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
                mcMMO.p.getLogger().severe("No defined level cap for " + primarySkillType.toString() + " - Contact the mcMMO dev team!");
                return false;
        }
    }
}
