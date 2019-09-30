package com.gmail.nossr50.config.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigSectionPartyItemWeights {

    private static final HashMap<Material, Integer> ITEM_WEIGHT_MAP_DEFAULT;

    static {
        ITEM_WEIGHT_MAP_DEFAULT = new HashMap<>();

        ITEM_WEIGHT_MAP_DEFAULT.put(QUARTZ, 200);
        ITEM_WEIGHT_MAP_DEFAULT.put(NETHER_QUARTZ_ORE, 200);
        ITEM_WEIGHT_MAP_DEFAULT.put(EMERALD_ORE, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(EMERALD, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND, 100);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_ORE, 100);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLD_INGOT, 50);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLD_ORE, 50);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_ORE, 40);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_INGOT, 40);
        ITEM_WEIGHT_MAP_DEFAULT.put(LAPIS_ORE, 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(REDSTONE, 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(REDSTONE_ORE, 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(GLOWSTONE_DUST, 20);
        ITEM_WEIGHT_MAP_DEFAULT.put(COAL, 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(COAL_ORE, 10);

        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_SHOVEL, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_SWORD, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_AXE, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_HOE, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_CHESTPLATE, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_HELMET, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_LEGGINGS, 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(DIAMOND_BOOTS, 150);

        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_SHOVEL, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_SWORD, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_AXE, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_HOE, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_CHESTPLATE, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_HELMET, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_LEGGINGS, 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(GOLDEN_BOOTS, 75);

        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_SHOVEL, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_SWORD, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_AXE, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_HOE, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_CHESTPLATE, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_HELMET, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_LEGGINGS, 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(IRON_BOOTS, 60);

        ITEM_WEIGHT_MAP_DEFAULT.put(LEATHER_CHESTPLATE, 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(LEATHER_HELMET, 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(LEATHER_BOOTS, 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(LEATHER_LEGGINGS, 10);
    }

    @Setting(value = "Party-Item-Share-Weight-Values",
            comment = "These weight values help control item distribution when using \"EQUAL\" distribution")
    private HashMap<Material, Integer> itemShareMap = ITEM_WEIGHT_MAP_DEFAULT;

    public HashMap<Material, Integer> getItemShareMap() {
        return itemShareMap;
    }
}