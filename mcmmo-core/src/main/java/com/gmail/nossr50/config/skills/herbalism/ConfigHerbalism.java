package com.gmail.nossr50.config.skills.herbalism;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;

@ConfigSerializable
public class ConfigHerbalism {

    private static final HashSet<String> DEFAULT_BONUS_DROPS;

    static {
        DEFAULT_BONUS_DROPS = new HashSet<>();

        DEFAULT_BONUS_DROPS.add("minecraft:beetroots");
        DEFAULT_BONUS_DROPS.add("minecraft:beetroot");
        DEFAULT_BONUS_DROPS.add("minecraft:brown_mushroom");
        DEFAULT_BONUS_DROPS.add("minecraft:cactus");
        DEFAULT_BONUS_DROPS.add("minecraft:carrots");
        DEFAULT_BONUS_DROPS.add("minecraft:carrot");
        DEFAULT_BONUS_DROPS.add("minecraft:cocoa");
        DEFAULT_BONUS_DROPS.add("minecraft:cocoa_beans");
        DEFAULT_BONUS_DROPS.add("minecraft:wheat");
        DEFAULT_BONUS_DROPS.add("minecraft:melon");
        DEFAULT_BONUS_DROPS.add("minecraft:melon_slice");
        DEFAULT_BONUS_DROPS.add("minecraft:potatoes");
        DEFAULT_BONUS_DROPS.add("minecraft:potato");
        DEFAULT_BONUS_DROPS.add("minecraft:pumpkin");
        DEFAULT_BONUS_DROPS.add("minecraft:red_mushroom");
        DEFAULT_BONUS_DROPS.add("minecraft:sugar_cane");
        DEFAULT_BONUS_DROPS.add("minecraft:vine");
        DEFAULT_BONUS_DROPS.add("minecraft:lily_pad");
        DEFAULT_BONUS_DROPS.add("minecraft:red_tulip");
        DEFAULT_BONUS_DROPS.add("minecraft:white_tulip");
        DEFAULT_BONUS_DROPS.add("minecraft:pink_tulip");
        DEFAULT_BONUS_DROPS.add("minecraft:orange_tulip");
        DEFAULT_BONUS_DROPS.add("minecraft:dandelion");
        DEFAULT_BONUS_DROPS.add("minecraft:poppy");
        DEFAULT_BONUS_DROPS.add("minecraft:blue_orchid");
        DEFAULT_BONUS_DROPS.add("minecraft:allium");
        DEFAULT_BONUS_DROPS.add("minecraft:azure_bluet");
        DEFAULT_BONUS_DROPS.add("minecraft:oxeye_daisy");
        DEFAULT_BONUS_DROPS.add("minecraft:sunflower");
        DEFAULT_BONUS_DROPS.add("minecraft:lilac");
        DEFAULT_BONUS_DROPS.add("minecraft:rose_bush");
        DEFAULT_BONUS_DROPS.add("minecraft:peony");

    }

    @Setting(value = "Bonus-Drops", comment = "The list of whitelisted bonus drops." +
            "\nInclude both the source block and drops that can be doubled" +
            "\nHerbalism and other gathering skills offer a chance to get extra drops when harvesting the block.")
    private HashSet<String> herbalismDoubleDropWhiteList = DEFAULT_BONUS_DROPS;

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigHerbalismSubSkills configHerbalismSubSkills = new ConfigHerbalismSubSkills();

    public HashSet<String> getBonusDrops() {
        return herbalismDoubleDropWhiteList;
    }

    public ConfigHerbalismSubSkills getConfigHerbalismSubSkills() {
        return configHerbalismSubSkills;
    }

    public ConfigHerbalismDoubleDrops getDoubleDrops() {
        return configHerbalismSubSkills.getDoubleDrops();
    }

    public ConfigHerbalismGreenThumb getGreenThumb() {
        return configHerbalismSubSkills.getGreenThumb();
    }

    public ConfigHerbalismHylianLuck getHylianLuck() {
        return configHerbalismSubSkills.getHylianLuck();
    }

    public ConfigHerbalismShroomThumb getShroomThumb() {
        return configHerbalismSubSkills.getShroomThumb();
    }
}