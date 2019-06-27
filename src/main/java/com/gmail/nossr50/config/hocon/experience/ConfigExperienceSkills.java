package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.experience.SpecialXPKey;
import com.gmail.nossr50.datatypes.skills.ItemMaterialCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceSkills {
    @Setting(value = "Acrobatics", comment = "XP Settings for Acrobatics")
    private ConfigExperienceAcrobatics experienceAcrobatics = new ConfigExperienceAcrobatics();

    @Setting(value = "Alchemy", comment = "XP Settings for Alchemy")
    private ConfigExperienceAlchemy experienceAlchemy = new ConfigExperienceAlchemy();

    @Setting(value = "Archery", comment = "XP Settings for Archery")
    private ConfigExperienceArchery experienceArchery = new ConfigExperienceArchery();

    @Setting(value = "Fishing", comment = "XP Settings for Fishing")
    private ConfigExperienceFishing experienceFishing = new ConfigExperienceFishing();

    @Setting(value = "Excavation", comment = "XP Settings for Excavation")
    private ConfigExperienceExcavation experienceExcavation = new ConfigExperienceExcavation();

    @Setting(value = "Woodcutting", comment = "XP Settings for Woodcutting")
    private ConfigExperienceWoodcutting experienceWoodcutting = new ConfigExperienceWoodcutting();

    @Setting(value = "Herbalism", comment = "XP Settings for Herbalism")
    private ConfigExperienceHerbalism experienceHerbalism = new ConfigExperienceHerbalism();

    @Setting(value = "Mining", comment = "XP Settings for Mining")
    private ConfigExperienceMining experienceMining = new ConfigExperienceMining();

    @Setting(value = "Repair", comment = "XP Settings for Repair")
    private ConfigExperienceRepair experienceRepair = new ConfigExperienceRepair();

    @Setting(value = "Smelting", comment = "XP Settings for Smelting")
    private ConfigExperienceSmelting experienceSmelting = new ConfigExperienceSmelting();

    @Setting(value = "Taming", comment = "XP Settings for Taming")
    private ConfigExperienceTaming experienceTaming = new ConfigExperienceTaming();

    @Setting(value = "Z-Combat", comment = "XP Settings for Combat")
    private ConfigExperienceCombat experienceCombat = new ConfigExperienceCombat();

    public boolean isReduceTreeFellerXP() {
        return experienceWoodcutting.isReduceTreeFellerXP();
    }

    /*
     * BOILER PLATE GETTERS
     */

    public ConfigExperienceAcrobatics getExperienceAcrobatics() {
        return experienceAcrobatics;
    }

    public ConfigExperienceAlchemy getExperienceAlchemy() {
        return experienceAlchemy;
    }

    public ConfigExperienceArchery getExperienceArchery() {
        return experienceArchery;
    }

    public ConfigExperienceFishing getExperienceFishing() {
        return experienceFishing;
    }

    public ConfigExperienceExcavation getExperienceExcavation() {
        return experienceExcavation;
    }

    public ConfigExperienceWoodcutting getExperienceWoodcutting() {
        return experienceWoodcutting;
    }

    public ConfigExperienceHerbalism getExperienceHerbalism() {
        return experienceHerbalism;
    }

    public ConfigExperienceMining getExperienceMining() {
        return experienceMining;
    }

    public ConfigExperienceRepair getExperienceRepair() {
        return experienceRepair;
    }

    public ConfigExperienceSmelting getExperienceSmelting() {
        return experienceSmelting;
    }

    public ConfigExperienceTaming getExperienceTaming() {
        return experienceTaming;
    }

    public ConfigExperienceCombat getExperienceCombat() {
        return experienceCombat;
    }

    public HashMap<String, Integer> getTamingExperienceMap() {
        return experienceTaming.getTamingExperienceMap();
    }

    public HashMap<String, Integer> getMiningExperienceMap() {
        return experienceMining.getMiningExperienceMap();
    }

    public HashMap<String, Integer> getSmeltingExperienceMap() {
        return experienceSmelting.getSmeltingExperienceMap();
    }

    public double getItemMaterialXPMultiplier(ItemMaterialCategory itemMaterialCategory) {
        return experienceRepair.getItemMaterialXPMultiplier(itemMaterialCategory);
    }

    public double getRepairXPBase() {
        return experienceRepair.getRepairXPBase();
    }

    public HashMap<String, Integer> getAcrobaticsXPMap() {
        return experienceAcrobatics.getAcrobaticsXPMap();
    }

    public double getFeatherFallMultiplier() {
        return experienceAcrobatics.getFeatherFallMultiplier();
    }

    public HashMap<String, Integer> getAlchemyXPMap() {
        return experienceAlchemy.getAlchemyXPMap();
    }

    public int getDodgeXP() {
        return experienceAcrobatics.getDodgeXP();
    }

    public int getRollXP() {
        return experienceAcrobatics.getRollXP();
    }

    public int getFallXP() {
        return experienceAcrobatics.getFallXP();
    }

    public int getStageOnePotionXP() {
        return experienceAlchemy.getStageOnePotionXP();
    }

    public int getStageTwoPotionXP() {
        return experienceAlchemy.getStageTwoPotionXP();
    }

    public int getStageThreePotionXP() {
        return experienceAlchemy.getStageThreePotionXP();
    }

    public int getStageFourPotionXP() {
        return experienceAlchemy.getStageFourPotionXP();
    }

    public int getPotionXPByStage(int potionStage) {
        return experienceAlchemy.getPotionXPByStage(potionStage);
    }

    public boolean isPvpXPEnabled() {
        return experienceCombat.isPvpXPEnabled();
    }

    public HashMap<String, Double> getCombatExperienceMap() {
        return experienceCombat.getCombatExperienceMap();
    }

    public HashMap<SpecialXPKey, Double> getSpecialCombatExperienceMap() {
        return experienceCombat.getSpecialCombatExperienceMap();
    }

    public double getDistanceMultiplier() {
        return experienceArchery.getDistanceMultiplier();
    }

    public HashMap<String, Integer> getHerbalismXPMap() {
        return experienceHerbalism.getHerbalismXPMap();
    }

    public HashMap<String, Integer> getWoodcuttingExperienceMap() {
        return experienceWoodcutting.getWoodcuttingExperienceMap();
    }

    public HashMap<String, Integer> getExcavationExperienceMap() {
        return experienceExcavation.getExcavationExperienceMap();
    }

    public HashMap<String, Integer> getFishingXPMap() {
        return experienceFishing.getFishingXPMap();
    }

    public int getShakeXP() {
        return experienceFishing.getShakeXP();
    }
}
