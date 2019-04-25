package com.gmail.nossr50.config.hocon.skills.repair;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigRepairArcaneForging {

    public static final boolean LOSE_ENCHANTS_DEFAULT = true;
    public static final boolean DOWNGRADES_ENABLED_DEFAULT = true;

    public static final HashMap<Integer, Double> KEEP_ENCHANT_MAP;
    public static final HashMap<Integer, Double> DOWNGRADE_MAP_DEFAULT;

    static {
        KEEP_ENCHANT_MAP = new HashMap<>();
        KEEP_ENCHANT_MAP.put(1, 10.0D);
        KEEP_ENCHANT_MAP.put(2, 20.0D);
        KEEP_ENCHANT_MAP.put(3, 30.0D);
        KEEP_ENCHANT_MAP.put(4, 40.0D);
        KEEP_ENCHANT_MAP.put(5, 50.0D);
        KEEP_ENCHANT_MAP.put(6, 55.0D);
        KEEP_ENCHANT_MAP.put(7, 60.0D);
        KEEP_ENCHANT_MAP.put(8, 65.0D);

        DOWNGRADE_MAP_DEFAULT = new HashMap<>();
        DOWNGRADE_MAP_DEFAULT.put(1, 75.0D);
        DOWNGRADE_MAP_DEFAULT.put(2, 50.0D);
        DOWNGRADE_MAP_DEFAULT.put(3, 40.0D);
        DOWNGRADE_MAP_DEFAULT.put(4, 30.0D);
        DOWNGRADE_MAP_DEFAULT.put(5, 25.0D);
        DOWNGRADE_MAP_DEFAULT.put(6, 20.0D);
        DOWNGRADE_MAP_DEFAULT.put(7, 15.0D);
        DOWNGRADE_MAP_DEFAULT.put(8, 10.0D);
    }

    @Setting(value = "May-Lose-Enchants", comment = "With this on, players have a chance to have enchantments stripped from" +
            "\n their item when repairing." +
            "\nThe odds to lose your enchants decrease with higher levels of skill." +
            "\nDefault value: " + LOSE_ENCHANTS_DEFAULT)
    private boolean mayLoseEnchants = LOSE_ENCHANTS_DEFAULT;

    @Setting(value = "Chance-To-Downgrade-Enchants", comment = "With this on, players have a chance to have enchants downgraded when repairing." +
            "\nThe chance to downgrade decreases with higher levels of skill." +
            "\nDefault value: " + DOWNGRADES_ENABLED_DEFAULT)
    private boolean downgradesEnabled = DOWNGRADES_ENABLED_DEFAULT;

    @Setting(value = "Downgrade-Chance-Per-Rank", comment = "Chance to downgrade enchants per rank in this Sub-Skill.")
    private HashMap<Integer, Double> downgradeChanceMap = DOWNGRADE_MAP_DEFAULT;

    @Setting(value = "Keep-Enchant-Chance-Per-Rank", comment = "Chance to keep the enchant without any downgrades per rank in this Sub-Skill")
    private HashMap<Integer, Double> keepEnchantChanceMap = KEEP_ENCHANT_MAP;

    public HashMap<Integer, Double> getDowngradeChanceMap() {
        return downgradeChanceMap;
    }

    public HashMap<Integer, Double> getKeepEnchantChanceMap() {
        return keepEnchantChanceMap;
    }

    public boolean isMayLoseEnchants() {
        return mayLoseEnchants;
    }

    public boolean isDowngradesEnabled() {
        return downgradesEnabled;
    }

}