package com.gmail.nossr50.config.hocon.mobs;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionCombat {

    @Setting(value = "Mob-Health-Bars", comment = "Health bars appear over a mobs world model when they are damaged by a player," +
            "\nTypically this is a visually representation of their health using hearts.")
    private ConfigSectionHealthBars healthBars = new ConfigSectionHealthBars();

    public ConfigSectionHealthBars getHealthBars() {
        return healthBars;
    }
}