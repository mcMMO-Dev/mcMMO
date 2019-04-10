package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigExperience {

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
}