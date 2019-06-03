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
}
