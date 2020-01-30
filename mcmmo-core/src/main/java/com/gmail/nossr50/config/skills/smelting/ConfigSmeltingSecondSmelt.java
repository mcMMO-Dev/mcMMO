package com.gmail.nossr50.config.skills.smelting;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigSmeltingSecondSmelt {

    private final static HashMap<Integer, Integer> XP_MULT_MAP_DEFAULT;

    static {
        XP_MULT_MAP_DEFAULT = new HashMap<>();
        XP_MULT_MAP_DEFAULT.put(1, 1);
        XP_MULT_MAP_DEFAULT.put(2, 2);
        XP_MULT_MAP_DEFAULT.put(3, 3);
        XP_MULT_MAP_DEFAULT.put(4, 3);
        XP_MULT_MAP_DEFAULT.put(5, 4);
        XP_MULT_MAP_DEFAULT.put(6, 4);
        XP_MULT_MAP_DEFAULT.put(7, 5);
        XP_MULT_MAP_DEFAULT.put(8, 5);
    }

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = 50.0;

    public double getMaxChance() {
        return maxChance;
    }

    @Setting(value = "XP-Multiplier-Per-Rank")
    private HashMap<Integer, Integer> xpMultiplierTable = XP_MULT_MAP_DEFAULT;

    public HashMap<Integer, Integer> getXpMultiplierTable() {
        return xpMultiplierTable;
    }
}
