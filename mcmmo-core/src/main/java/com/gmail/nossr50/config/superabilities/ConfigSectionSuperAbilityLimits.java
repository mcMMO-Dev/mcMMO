package com.gmail.nossr50.config.superabilities;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionSuperAbilityLimits {
    public static final int TOOL_DURABILITY_DAMAGE_DEFAULT = 1;
    @Setting(value = "Tree-Feller", comment = "Options for Tree Feller")
    private ConfigSectionTreeFeller treeFeller = new ConfigSectionTreeFeller();

    @Setting(value = "Tool-Durability-Damage", comment = "Increase this number to cause more durability loss for tools when using super abilities." +
            "\nDefault value: " + TOOL_DURABILITY_DAMAGE_DEFAULT)
    private int toolDurabilityDamage = TOOL_DURABILITY_DAMAGE_DEFAULT;

    public ConfigSectionTreeFeller getTreeFeller() {
        return treeFeller;
    }

    public int getToolDurabilityDamage() {
        return toolDurabilityDamage;
    }
}