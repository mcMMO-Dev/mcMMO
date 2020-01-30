package com.gmail.nossr50.config.skills.herbalism;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigHerbalismHylianLuck {

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 10.0;
}
