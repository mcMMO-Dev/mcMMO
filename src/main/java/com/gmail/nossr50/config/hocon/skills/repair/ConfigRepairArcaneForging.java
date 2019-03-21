package com.gmail.nossr50.config.hocon.skills.repair;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigRepairArcaneForging {

    public static final boolean LOSE_ENCHANTS_DEFAULT = true;
    public static final boolean DOWNGRADES_ENABLED_DEFAULT = true;
    @Setting(value = "May-Lose-Enchants", comment = "With this on, players have a chance to have enchantments stripped from" +
            "\n    their item when repairing." +
            "\nThe odds to lose your enchants decrease with higher levels of skill." +
            "\nDefault value: "+LOSE_ENCHANTS_DEFAULT)
    private boolean mayLoseEnchants = LOSE_ENCHANTS_DEFAULT;

    @Setting(value = "Chance-To-Downgrade-Enchants", comment = "With this on, players have a chance to have enchants downgraded when repairing." +
            "\nThe chance to downgrade decreases with higher levels of skill." +
            "\nDefault value: "+DOWNGRADES_ENABLED_DEFAULT)
    private boolean downgradesEnabled = DOWNGRADES_ENABLED_DEFAULT;

    public boolean isMayLoseEnchants() {
        return mayLoseEnchants;
    }

    public boolean isDowngradesEnabled() {
        return downgradesEnabled;
    }
}