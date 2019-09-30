package com.gmail.nossr50.config.hocon.skills.unarmed;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigUnarmed {

    private static final boolean ITEMS_COUNT_AS_UNARMED_DEFAULT = true;

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigUnarmedSubSkills subskills = new ConfigUnarmedSubSkills();

    @Setting(value = "Items-Count-As-Unarmed", comment = "Unarmed skills will work even if you are holding an item." +
            "\nMost tools are not considered an item, for example pickaxes, tridents, swords, etc won't count as unarmed with this setting enabled." +
            "\nDefault value: "+ITEMS_COUNT_AS_UNARMED_DEFAULT)
    private boolean doItemsCountAsUnarmed = ITEMS_COUNT_AS_UNARMED_DEFAULT;

    public ConfigUnarmedSubSkills getSubSkills() {
        return subskills;
    }

    public ConfigUnarmedDisarm getDisarm() {
        return subskills.getDisarm();
    }

    public boolean doesDisarmPreventTheft() {
        return subskills.isPreventItemTheft();
    }

    public boolean doItemsCountAsUnarmed() {
        return doItemsCountAsUnarmed;
    }
}