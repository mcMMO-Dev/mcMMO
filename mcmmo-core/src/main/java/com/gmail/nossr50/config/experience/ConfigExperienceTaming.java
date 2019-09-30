package com.gmail.nossr50.config.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceTaming {

    private final static HashMap<String, Integer> TAMING_EXPERIENCE_DEFAULT;

    static {
        TAMING_EXPERIENCE_DEFAULT = new HashMap<>();

        TAMING_EXPERIENCE_DEFAULT.put("wolf", 250);
        TAMING_EXPERIENCE_DEFAULT.put("ocelot", 500);
        TAMING_EXPERIENCE_DEFAULT.put("cat", 500);
        TAMING_EXPERIENCE_DEFAULT.put("horse", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("donkey", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("mule", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("skeleton_horse", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("zombie_horse", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("llama", 1200);
        TAMING_EXPERIENCE_DEFAULT.put("parrot", 1100);
        TAMING_EXPERIENCE_DEFAULT.put("fox", 1000);
        TAMING_EXPERIENCE_DEFAULT.put("panda", 1000);
    }

    @Setting(value = "Taming-XP-Values")
    private HashMap<String, Integer> tamingExperienceMap = TAMING_EXPERIENCE_DEFAULT;

    public HashMap<String, Integer> getTamingExperienceMap() {
        return tamingExperienceMap;
    }
}