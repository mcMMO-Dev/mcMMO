package com.gmail.nossr50.config.skills.herbalism;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigHerbalismShroomThumb {

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 50.0;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

}
