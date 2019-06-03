package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceWoodcutting {

    private static final HashMap<String, Integer> WOODCUTTING_EXPERIENCE_DEFAULT;

    static {
        WOODCUTTING_EXPERIENCE_DEFAULT = new HashMap<>();

        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:oak_log", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:spruce_log", 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:birch_log", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:jungle_log", 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:acacia_log", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:dark_oak_log", 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_oak_log", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_spruce_log", 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_birch_log", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_jungle_log", 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_acacia_log", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_dark_oak_log", 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:oak_wood", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:spruce_wood", 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:birch_wood", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:jungle_wood", 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:acacia_wood", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:dark_oak_wood", 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_oak_wood", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_spruce_wood", 80);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_birch_wood", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_jungle_wood", 100);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_acacia_wood", 90);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:stripped_dark_oak_wood", 90);

        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:red_mushroom_block", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:brown_mushroom_block", 70);
        WOODCUTTING_EXPERIENCE_DEFAULT.put("minecraft:mushroom_stem", 80);
    }

    @Setting(value = "Woodcutting-Experience")
    private HashMap<String, Integer> woodcuttingExperienceMap = WOODCUTTING_EXPERIENCE_DEFAULT;

    public HashMap<String, Integer> getWoodcuttingExperienceMap() {
        return woodcuttingExperienceMap;
    }
}
