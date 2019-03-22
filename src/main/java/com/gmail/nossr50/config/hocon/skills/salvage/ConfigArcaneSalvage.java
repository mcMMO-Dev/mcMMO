package com.gmail.nossr50.config.hocon.skills.salvage;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigArcaneSalvage {
    public static final HashMap<Integer, Double> FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT;
    public static final HashMap<Integer, Double> PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT;

    static {
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT = new HashMap<>();
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT = new HashMap<>();

        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(1, 2.5);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(2, 5.0);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(3, 7.5);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(4, 10.0);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(5, 12.5);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(6, 17.5);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(7, 25.0);
        FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(8, 32.5);

        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(1, 2.0);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(2, 2.5);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(3, 5.0);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(4, 7.5);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(5, 10.0);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(6, 12.5);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(7, 15.0);
        PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT.put(8, 17.5);
    }

    @Setting(value = "Extract-Full-Enchant-Percentage-Chance-Per-Rank", comment = "The chance to extract the enchantment from the item during salvage without any downgrade." +
            "\nIf this fails, a check to see if you can extract a downgraded version of the enchantment executes.")
    private HashMap<Integer, Double> extractFullEnchantChance = FULL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT;

    @Setting(value = "Extract-Partial-Enchant-Percentage-Chance-Per-Rank", comment = "The chance to extract a downgraded enchantment from the item during salvage." +
            "\nThis check happens if you fail to extract the full enchant from the item.")
    private HashMap<Integer, Double> extractPartialEnchantChance = PARTIAL_ENCHANT_INTEGER_DOUBLE_HASH_MAP_DEFAULT;


    public HashMap<Integer, Double> getExtractFullEnchantChance() {
        return extractFullEnchantChance;
    }

    public HashMap<Integer, Double> getExtractPartialEnchantChance() {
        return extractPartialEnchantChance;
    }
}