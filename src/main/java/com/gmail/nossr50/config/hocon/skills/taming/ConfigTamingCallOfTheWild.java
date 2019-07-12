package com.gmail.nossr50.config.hocon.skills.taming;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;


@ConfigSerializable
public class ConfigTamingCallOfTheWild {

    @Setting(value = "Cat-Summon-Item-Name", comment = "The name of the item used to summon cats.")
    private String cotwCatItem = "minecraft:raw_cod";
    private String cotwWolfItem = "minecraft:bone";
    private String cotwHorseItem = "minecraft:apple";

    public String getCotwCatItem() {
        return cotwCatItem;
    }

    public String getCotwWolfItem() {
        return cotwWolfItem;
    }

    public String getCotwHorseItem() {
        return cotwHorseItem;
    }
}
