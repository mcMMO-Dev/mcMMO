package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperience {

    private static final double GLOBAL_XP_MULT_DEFAULT = 1.0D;

    @Setting(value = "Global-XP-Multiplier", comment = "This multiplier is applied at the very end of every XP gain, you can use it as a shortcut to increase or decrease xp gains across the entire plugin" +
            "\nThis value is temporarily overridden by xprate events." +
            "\nDefault value: " + GLOBAL_XP_MULT_DEFAULT)
    private double globalXPMultiplier = GLOBAL_XP_MULT_DEFAULT;

    @Setting(value = "Global-Skill-XP-Multipliers", comment = "This multiplier is applied at the very end of an XP calculation specific to its corresponding skill, this value is applied before the global multiplier is applied.")
    private ConfigExperienceSkillMultiplier configExperienceSkillMultiplier = new ConfigExperienceSkillMultiplier();

    @Setting(value = "Skill-XP-Settings", comment = "XP values and multipliers for each skill")
    private ConfigExperienceSkills configExperienceSkills = new ConfigExperienceSkills();

    /*
     * BOILER PLATE GETTERS
     */

    public ConfigExperienceSkillMultiplier getConfigExperienceSkillMultiplier() {
        return configExperienceSkillMultiplier;
    }

    public ConfigExperienceAcrobatics getExperienceAcrobatics() {
        return getConfigExperienceSkills().getExperienceAcrobatics();
    }

    public ConfigExperienceAlchemy getExperienceAlchemy() {
        return getConfigExperienceSkills().getExperienceAlchemy();
    }

    public ConfigExperienceArchery getExperienceArchery() {
        return getConfigExperienceSkills().getExperienceArchery();
    }

    public ConfigExperienceFishing getExperienceFishing() {
        return getConfigExperienceSkills().getExperienceFishing();
    }

    public ConfigExperienceExcavation getExperienceExcavation() {
        return getConfigExperienceSkills().getExperienceExcavation();
    }

    public ConfigExperienceWoodcutting getExperienceWoodcutting() {
        return getConfigExperienceSkills().getExperienceWoodcutting();
    }

    public ConfigExperienceHerbalism getExperienceHerbalism() {
        return getConfigExperienceSkills().getExperienceHerbalism();
    }

    public ConfigExperienceMining getExperienceMining() {
        return getConfigExperienceSkills().getExperienceMining();
    }

    public ConfigExperienceRepair getExperienceRepair() {
        return getConfigExperienceSkills().getExperienceRepair();
    }

    public ConfigExperienceSmelting getExperienceSmelting() {
        return getConfigExperienceSkills().getExperienceSmelting();
    }

    public ConfigExperienceTaming getExperienceTaming() {
        return getConfigExperienceSkills().getExperienceTaming();
    }

    public ConfigExperienceCombat getExperienceCombat() {
        return getConfigExperienceSkills().getExperienceCombat();
    }

    public HashMap<String, Integer> getTamingExperienceMap() {
        return getConfigExperienceSkills().getTamingExperienceMap();
    }

    public HashMap<String, Integer> getMiningExperienceMap() {
        return getConfigExperienceSkills().getMiningExperienceMap();
    }

    public HashMap<String, Integer> getSmeltingExperienceMap() {
        return getConfigExperienceSkills().getSmeltingExperienceMap();
    }

    public HashMap<String, Double> getItemMaterialXPMultiplier() {
        return getConfigExperienceSkills().getItemMaterialXPMultiplier();
    }

    public double getRepairXPBase() {
        return getConfigExperienceSkills().getRepairXPBase();
    }

    public HashMap<String, Integer> getAcrobaticsXPMap() {
        return getConfigExperienceSkills().getAcrobaticsXPMap();
    }

    public Double getFeatherFallMultiplier() {
        return getConfigExperienceSkills().getFeatherFallMultiplier();
    }

    public HashMap<String, Integer> getAlchemyXPMap() {
        return getConfigExperienceSkills().getAlchemyXPMap();
    }

    public int getDodgeXP() {
        return getConfigExperienceSkills().getDodgeXP();
    }

    public int getRollXP() {
        return getConfigExperienceSkills().getRollXP();
    }

    public int getFallXP() {
        return getConfigExperienceSkills().getFallXP();
    }

    public int getStageOnePotionXP() {
        return getConfigExperienceSkills().getStageOnePotionXP();
    }

    public int getStageTwoPotionXP() {
        return getConfigExperienceSkills().getStageTwoPotionXP();
    }

    public int getStageThreePotionXP() {
        return getConfigExperienceSkills().getStageThreePotionXP();
    }

    public int getStageFourPotionXP() {
        return getConfigExperienceSkills().getStageFourPotionXP();
    }

    public int getPotionXPByStage(int potionStage) {
        return getConfigExperienceSkills().getPotionXPByStage(potionStage);
    }

    public boolean isPvpXPEnabled() {
        return getConfigExperienceSkills().isPvpXPEnabled();
    }

    public HashMap<String, Double> getCombatExperienceMap() {
        return getConfigExperienceSkills().getCombatExperienceMap();
    }

    public double getDistanceMultiplier() {
        return getConfigExperienceSkills().getDistanceMultiplier();
    }

    public HashMap<String, Integer> getHerbalismXPMap() {
        return getConfigExperienceSkills().getHerbalismXPMap();
    }

    public HashMap<String, Integer> getWoodcuttingExperienceMap() {
        return getConfigExperienceSkills().getWoodcuttingExperienceMap();
    }

    public HashMap<String, Integer> getExcavationExperienceMap() {
        return getConfigExperienceSkills().getExcavationExperienceMap();
    }

    public HashMap<String, Integer> getFishingXPMap() {
        return getConfigExperienceSkills().getFishingXPMap();
    }

    public int getShakeXP() {
        return getConfigExperienceSkills().getShakeXP();
    }

    public double getSpawnedMobXPMult() {
        return getConfigExperienceSkills().getSpawnedMobXPMult();
    }

    public double getPVPXPMult() {
        return getConfigExperienceSkills().getPVPXPMult();
    }

    public double getAnimalsXPMult() {
        return getConfigExperienceSkills().getAnimalsXPMult();
    }

    public ConfigExperienceSkills getConfigExperienceSkills() {
        return configExperienceSkills;
    }

    public double getGlobalXPMultiplier() {
        return globalXPMultiplier;
    }
}