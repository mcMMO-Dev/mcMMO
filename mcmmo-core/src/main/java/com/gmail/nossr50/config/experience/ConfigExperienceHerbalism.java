package com.gmail.nossr50.config.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceHerbalism {

    private final static HashMap<String, Integer> HERBALISM_EXPERIENCE_DEFAULT;

    static {
        HERBALISM_EXPERIENCE_DEFAULT = new HashMap<>();

        /* UNDER THE SEA */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:seagrass", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:tall_seagrass", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:kelp", 3);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:kelp_plant", 3);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:tube_coral", 80);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:brain_coral", 90);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:bubble_coral", 75);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:fire_coral", 120);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:horn_coral", 175);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:tube_coral_fan", 80);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:brain_coral_fan", 90);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:bubble_coral_fan", 75);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:fire_coral_fan", 120);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:horn_coral_fan", 175);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:tube_coral_wall_fan", 80);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:brain_coral_wall_fan", 90);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:bubble_coral_wall_fan", 75);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:fire_coral_wall_fan", 120);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:horn_coral_wall_fan", 175);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_tube_coral", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_brain_coral", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_bubble_coral", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_fire_coral", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_horn_coral", 30);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_tube_coral_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_brain_coral_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_bubble_coral_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_fire_coral_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_horn_coral_fan", 30);

        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_tube_coral_wall_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_brain_coral_wall_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_bubble_coral_wall_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_fire_coral_wall_fan", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_horn_coral_wall_fan", 30);

        /* BACK TO DRY LAND */

        /* FLOWERS */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:allium", 300);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:azure_bluet", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:blue_orchid", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:lilac", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:orange_tulip", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:oxeye_daisy", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:peony", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:pink_tulip", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:white_tulip", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:poppy", 100);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dandelion", 100);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:red_tulip", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:rose_bush", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:sunflower", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:cornflower", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:lily_of_the_valley", 150);

        /* WEEDS */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:fern", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:large_fern", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:grass", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:tall_grass", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:vine", 10);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:dead_bush", 30);

        /* MISC */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:lily_pad", 100);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:sweet_berry_bush", 300);

        /* MUSHROOMS */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:red_mushroom", 150);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:brown_mushroom", 150);

        /* CROPS */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:beetroots", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:carrots", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:cactus", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:cocoa", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:potatoes", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:pumpkin", 20);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:melon", 20);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:wheat", 50);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:sugar_cane", 30);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:nether_wart", 30);

        /* JUNGLE */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:bamboo", 10);

        /* END PLANTS */
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:chorus_plant", 1);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:chorus_flower", 25);
        HERBALISM_EXPERIENCE_DEFAULT.put("minecraft:wither_rose", 500);
    }

    @Setting(value = "Herbalism-Experience")
    private HashMap<String, Integer> herbalismXPMap = HERBALISM_EXPERIENCE_DEFAULT;

    public HashMap<String, Integer> getHerbalismXPMap() {
        return herbalismXPMap;
    }
}