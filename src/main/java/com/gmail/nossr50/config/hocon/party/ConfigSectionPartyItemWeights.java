package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.config.hocon.HOCONUtil;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

import static org.bukkit.Material.*;

@ConfigSerializable
public class ConfigSectionPartyItemWeights {

    private static final HashMap<String, Integer> ITEM_WEIGHT_MAP_DEFAULT;

    static {
        ITEM_WEIGHT_MAP_DEFAULT = new HashMap<>();

        ITEM_WEIGHT_MAP_DEFAULT.put("Default", 5);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(QUARTZ), 200);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(NETHER_QUARTZ_ORE), 200);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(EMERALD_ORE), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(EMERALD), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND), 100);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_ORE), 100);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLD_INGOT), 50);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLD_ORE), 50);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_ORE), 40);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_INGOT), 40);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(LAPIS_ORE), 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(REDSTONE), 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(REDSTONE_ORE), 30);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GLOWSTONE_DUST), 20);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(COAL), 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(COAL_ORE), 10);

        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_SHOVEL), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_SWORD), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_AXE), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_HOE), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_CHESTPLATE), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_HELMET), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_LEGGINGS), 150);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(DIAMOND_BOOTS), 150);

        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_SHOVEL), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_SWORD), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_AXE), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_HOE), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_CHESTPLATE), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_HELMET), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_LEGGINGS), 75);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(GOLDEN_BOOTS), 75);

        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_SHOVEL), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_SWORD), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_AXE), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_HOE), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_CHESTPLATE), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_HELMET), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_LEGGINGS), 60);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(IRON_BOOTS), 60);

        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(LEATHER_CHESTPLATE), 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(LEATHER_HELMET), 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(LEATHER_BOOTS), 10);
        ITEM_WEIGHT_MAP_DEFAULT.put(getHOCONFriendly(LEATHER_LEGGINGS), 10);

    }

    @Setting(value = "Party-Item-Share-Weight-Values",
            comment = "These weight values help control item distribution when using \"EQUAL\" distribution")
    private HashMap<String, Integer> itemShareMap = ITEM_WEIGHT_MAP_DEFAULT;

    /**
     * Takes an input like 'NETHER_BRICK' and turns it into 'Nether-Brick'
     * @param material target Material to convert
     * @return a HOCON serializer friendly key name
     */
    private static String getHOCONFriendly(Material material)
    {
        return HOCONUtil.serializeENUMName(material.toString());
    }

    public HashMap<String, Integer> getItemShareMap() {
        return itemShareMap;
    }
}