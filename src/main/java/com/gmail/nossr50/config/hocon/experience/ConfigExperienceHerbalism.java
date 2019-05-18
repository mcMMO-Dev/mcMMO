package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceHerbalism {

    private final static HashMap<String, Integer> HERBALISM_EXPERIENCE_DEFAULT;

    static {
        HERBALISM_EXPERIENCE_DEFAULT = new HashMap<>();

        /* UNDER THE SEA */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.SEAGRASS.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.TALL_SEAGRASS.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.KELP.getKey().toString(), 3);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.KELP_PLANT.getKey().toString(), 3);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.TUBE_CORAL.getKey().toString(), 80);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BRAIN_CORAL.getKey().toString(), 90);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BUBBLE_CORAL.getKey().toString(), 75);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.FIRE_CORAL.getKey().toString(), 120);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.HORN_CORAL.getKey().toString(), 175);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.TUBE_CORAL_FAN.getKey().toString(), 80);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BRAIN_CORAL_FAN.getKey().toString(), 90);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BUBBLE_CORAL_FAN.getKey().toString(), 75);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.FIRE_CORAL_FAN.getKey().toString(), 120);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.HORN_CORAL_FAN.getKey().toString(), 175);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.TUBE_CORAL_WALL_FAN.getKey().toString(), 80);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BRAIN_CORAL_WALL_FAN.getKey().toString(), 90);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BUBBLE_CORAL_WALL_FAN.getKey().toString(), 75);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.FIRE_CORAL_WALL_FAN.getKey().toString(), 120);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.HORN_CORAL_WALL_FAN.getKey().toString(), 175);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_TUBE_CORAL.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BRAIN_CORAL.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BUBBLE_CORAL.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_FIRE_CORAL.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_HORN_CORAL.getKey().toString(), 30);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_TUBE_CORAL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BRAIN_CORAL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BUBBLE_CORAL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_FIRE_CORAL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_HORN_CORAL_FAN.getKey().toString(), 30);

        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_TUBE_CORAL_WALL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BRAIN_CORAL_WALL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BUBBLE_CORAL_WALL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_FIRE_CORAL_WALL_FAN.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_HORN_CORAL_WALL_FAN.getKey().toString(), 30);

        /* BACK TO DRY LAND */

        /* FLOWERS */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.ALLIUM.getKey().toString(), 300);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.AZURE_BLUET.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BLUE_ORCHID.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.LILAC.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.ORANGE_TULIP.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.OXEYE_DAISY.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.PEONY.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.PINK_TULIP.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.WHITE_TULIP.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.POPPY.getKey().toString(), 100);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DANDELION.getKey().toString(), 100);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.RED_TULIP.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.ROSE_BUSH.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.SUNFLOWER.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.CORNFLOWER.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.LILY_OF_THE_VALLEY.getKey().toString(), 150);

        /* WEEDS */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.FERN.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.LARGE_FERN.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.GRASS.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.TALL_GRASS.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.VINE.getKey().toString(), 10);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.DEAD_BUSH.getKey().toString(), 30);

        /* MISC */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.LILY_PAD.getKey().toString(), 100);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.SWEET_BERRY_BUSH.getKey().toString(), 300);

        /* MUSHROOMS */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.RED_MUSHROOM.getKey().toString(), 150);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BROWN_MUSHROOM.getKey().toString(), 150);

        /* CROPS */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.BEETROOTS.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.CARROTS.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.CACTUS.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.COCOA.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.POTATOES.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.PUMPKIN.getKey().toString(), 20);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.MELON.getKey().toString(), 20);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.WHEAT.getKey().toString(), 50);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.SUGAR_CANE.getKey().toString(), 30);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.NETHER_WART.getKey().toString(), 30);

        /* END PLANTS */
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.CHORUS_PLANT.getKey().toString(), 1);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.CHORUS_FLOWER.getKey().toString(), 25);
        HERBALISM_EXPERIENCE_DEFAULT.put(Material.WITHER_ROSE.getKey().toString(), 500);
    }

    @Setting(value = "Herbalism-Experience")
    private HashMap<String, Integer> herbalismXPMap = HERBALISM_EXPERIENCE_DEFAULT;

    public HashMap<String, Integer> getHerbalismXPMap() {
        return herbalismXPMap;
    }
}