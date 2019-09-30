package com.gmail.nossr50.config.skills.fishing;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigFishingInnerPeace {

    private static final HashMap<Integer, Integer> INNER_PEACE_DEFAULT;

    static {
        INNER_PEACE_DEFAULT = new HashMap<>();
        INNER_PEACE_DEFAULT.put(1, 3);
        INNER_PEACE_DEFAULT.put(2, 5);
        INNER_PEACE_DEFAULT.put(3, 7);
    }

    @Setting(value = "Vanilla-Orb-XP-Multipliers", comment = "How much Inner Peace will grant in bonus XP orbs" +
            "\nThis value is used to multiply the number of vanilla XP orbs you would normally receive.")
    private HashMap<Integer, Integer> innerPeaceVanillaXPMultiplier = INNER_PEACE_DEFAULT;

    public HashMap<Integer, Integer> getInnerPeaceVanillaXPMultiplier() {
        return innerPeaceVanillaXPMultiplier;
    }
}