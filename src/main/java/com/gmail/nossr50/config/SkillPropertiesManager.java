package com.gmail.nossr50.config;

import com.gmail.nossr50.config.hocon.skills.ConfigSubSkillScalingRNG;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;

import java.util.HashMap;

/**
 * Hacky way to do this until I rewrite the skill system fully
 */
public class SkillPropertiesManager {
    private HashMap<SubSkillType, Double> maxChanceMap;
    private HashMap<SubSkillType, Double> maxBonusLevelMap;
    private HashMap<SubSkillType, Double> maxBonusPercentage;

    public SkillPropertiesManager() {
        maxChanceMap = new HashMap<>();
        maxBonusLevelMap = new HashMap<>();
        maxBonusPercentage = new HashMap<>();
    }

    public void registerRNG(SubSkillType subSkillType, ConfigSubSkillScalingRNG config) {
        maxChanceMap.put(subSkillType, config.getMaxChance());
        maxBonusLevelMap.put(subSkillType, config.getMaxBonusLevel());
    }

    public double getMaxChance(SubSkillType subSkillType) {
        return maxChanceMap.get(subSkillType);
    }

    public double getMaxBonusLevel(SubSkillType subSkillType) {
        return maxBonusLevelMap.get(subSkillType);
    }

    public void fillRegisters() {

        fillRNGRegisters();
    }

    private void fillRNGRegisters() {
        //Acrobatics
        registerRNG(SubSkillType.ACROBATICS_DODGE, mcMMO.getConfigManager().getConfigAcrobatics().getDodge().getRNGSettings());
        registerRNG(SubSkillType.ACROBATICS_DODGE, mcMMO.getConfigManager().getConfigAcrobatics().getRoll().getRNGSettings());

        //Repair
        registerRNG(SubSkillType.REPAIR_SUPER_REPAIR, mcMMO.getConfigManager().getConfigRepair().getSuperRepair().getSuperRepair());
    }

}
