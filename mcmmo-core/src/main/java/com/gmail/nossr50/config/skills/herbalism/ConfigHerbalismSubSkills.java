package com.gmail.nossr50.config.skills.herbalism;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigHerbalismSubSkills {

    @Setting(value = "Double-Drops")
    private ConfigHerbalismDoubleDrops doubleDrops = new ConfigHerbalismDoubleDrops();

    @Setting(value = "Green-Thumb")
    private ConfigHerbalismGreenThumb greenThumb = new ConfigHerbalismGreenThumb();

    @Setting(value = "Hylian-Luck")
    private ConfigHerbalismHylianLuck hylianLuck = new ConfigHerbalismHylianLuck();

    @Setting(value = "Shroom-Thumb")
    private ConfigHerbalismShroomThumb shroomThumb = new ConfigHerbalismShroomThumb();


    public ConfigHerbalismDoubleDrops getDoubleDrops() {
        return doubleDrops;
    }

    public ConfigHerbalismGreenThumb getGreenThumb() {
        return greenThumb;
    }

    public ConfigHerbalismHylianLuck getHylianLuck() {
        return hylianLuck;
    }

    public ConfigHerbalismShroomThumb getShroomThumb() {
        return shroomThumb;
    }
}
