package com.gmail.nossr50.config.hocon.skills.ranks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRanks {

    @Setting(value = "Acrobatics", comment = "Configure when sub-skills unlock for Acrobatics here.")
    private ConfigRanksAcrobatics acrobatics = new ConfigRanksAcrobatics();

    @Setting(value = "Alchemy", comment = "Configure when sub-skills unlock for Alchemy here.")
    private ConfigRanksAlchemy alchemy = new ConfigRanksAlchemy();

    @Setting(value = "Archery", comment = "Configure when sub-skills unlock for Archery here.")
    private ConfigRanksArchery archery = new ConfigRanksArchery();

    @Setting(value = "Axes", comment = "Configure when sub-skills unlock for Axes here.")
    private ConfigRanksAxes axes = new ConfigRanksAxes();

    @Setting(value = "Taming", comment = "Configure when sub-skills unlock for Taming here.")
    private ConfigRanksTaming taming = new ConfigRanksTaming();

    @Setting(value = "Smelting", comment = "Configure when sub-skills unlock for Smelting here.")
    private ConfigRanksSmelting smelting = new ConfigRanksSmelting();

    @Setting(value = "Salvage", comment = "Configure when sub-skills unlock for Salvage here.")
    private ConfigRanksSalvage salvage = new ConfigRanksSalvage();

    @Setting(value = "Mining", comment = "Configure when sub-skills unlock for Mining here.")
    private ConfigRanksMining mining = new ConfigRanksMining();

    @Setting(value = "Herbalism", comment = "Configure when sub-skills unlock for Herbalism here.")
    private ConfigRanksHerbalism herbalism = new ConfigRanksHerbalism();

    @Setting(value = "Fishing", comment = "Configure when sub-skills unlock for Fishing here.")
    private ConfigRanksFishing fishing = new ConfigRanksFishing();

    @Setting(value = "Swords", comment = "Configure when sub-skills unlock for Swords here.")
    private ConfigRanksSwords swords = new ConfigRanksSwords();

    @Setting(value = "Unarmed", comment = "Configure when sub-skills unlock for Unarmed here.")
    private ConfigRanksUnarmed unarmed = new ConfigRanksUnarmed();

    @Setting(value = "Woodcutting", comment = "Configure when sub-skills unlock for Woodcutting here.")
    private ConfigRanksWoodcutting woodcutting = new ConfigRanksWoodcutting();

    @Setting(value = "Excavation", comment = "Configure when sub-skills unlock for Excavation here.")
    private ConfigRanksExcavation excavation = new ConfigRanksExcavation();

    @Setting(value = "Repair", comment = "Configure when sub-skills unlock for Repair here.")
    private ConfigRanksRepair repair = new ConfigRanksRepair();


}
