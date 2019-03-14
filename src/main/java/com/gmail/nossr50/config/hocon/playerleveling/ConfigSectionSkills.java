package com.gmail.nossr50.config.hocon.playerleveling;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSkills {

    /*
     * CONFIG NODES
     */

    @Setting(value = "Acrobatics")
    private ConfigSectionSkillLevelCap acrobatics = new ConfigSectionSkillLevelCap();

    @Setting(value = "Alchemy")
    private ConfigSectionSkillLevelCap alchemy = new ConfigSectionSkillLevelCap();

    @Setting(value = "Archery")
    private ConfigSectionSkillLevelCap archery = new ConfigSectionSkillLevelCap();

    @Setting(value = "Axes")
    private ConfigSectionSkillLevelCap axes = new ConfigSectionSkillLevelCap();

    @Setting(value = "Excavation")
    private ConfigSectionSkillLevelCap excavation = new ConfigSectionSkillLevelCap();

    @Setting(value = "Fishing")
    private ConfigSectionSkillLevelCap fishing = new ConfigSectionSkillLevelCap();

    @Setting(value = "Herbalism")
    private ConfigSectionSkillLevelCap herbalism = new ConfigSectionSkillLevelCap();

    @Setting(value = "Mining")
    private ConfigSectionSkillLevelCap mining = new ConfigSectionSkillLevelCap();

    @Setting(value = "Repair")
    private ConfigSectionSkillLevelCap repair = new ConfigSectionSkillLevelCap();

    @Setting(value = "Swords")
    private ConfigSectionSkillLevelCap swords = new ConfigSectionSkillLevelCap();

    @Setting(value = "Taming")
    private ConfigSectionSkillLevelCap taming = new ConfigSectionSkillLevelCap();

    @Setting(value = "Unarmed")
    private ConfigSectionSkillLevelCap unarmed = new ConfigSectionSkillLevelCap();

    @Setting(value = "Woodcutting")
    private ConfigSectionSkillLevelCap woodcutting = new ConfigSectionSkillLevelCap();

    @Setting(value = "Smelting")
    private ConfigSectionSkillLevelCap smelting = new ConfigSectionSkillLevelCap();

    @Setting(value = "Salvage")
    private ConfigSectionSkillLevelCap salvage = new ConfigSectionSkillLevelCap();

    /*
     * GETTER BOILERPLATE
     */

    public ConfigSectionSkillLevelCap getAcrobatics() {
        return acrobatics;
    }

    public ConfigSectionSkillLevelCap getAlchemy() {
        return alchemy;
    }

    public ConfigSectionSkillLevelCap getArchery() {
        return archery;
    }

    public ConfigSectionSkillLevelCap getAxes() {
        return axes;
    }

    public ConfigSectionSkillLevelCap getExcavation() {
        return excavation;
    }

    public ConfigSectionSkillLevelCap getFishing() {
        return fishing;
    }

    public ConfigSectionSkillLevelCap getHerbalism() {
        return herbalism;
    }

    public ConfigSectionSkillLevelCap getMining() {
        return mining;
    }

    public ConfigSectionSkillLevelCap getRepair() {
        return repair;
    }

    public ConfigSectionSkillLevelCap getSwords() {
        return swords;
    }

    public ConfigSectionSkillLevelCap getTaming() {
        return taming;
    }

    public ConfigSectionSkillLevelCap getUnarmed() {
        return unarmed;
    }

    public ConfigSectionSkillLevelCap getWoodcutting() {
        return woodcutting;
    }

    public ConfigSectionSkillLevelCap getSmelting() {
        return smelting;
    }

    public ConfigSectionSkillLevelCap getSalvage() {
        return salvage;
    }
}
