package com.gmail.nossr50.config.skills.taming;

import com.gmail.nossr50.datatypes.skills.properties.AbstractDamageProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTamingSharpenedClaws {

    @Setting(value = "Bonus-Damage", comment = "The amount of bonus damage Sharpened Claws will add.")
    private AbstractDamageProperty bonusDamage;

    public AbstractDamageProperty getBonusDamage() {
        return bonusDamage;
    }
}
