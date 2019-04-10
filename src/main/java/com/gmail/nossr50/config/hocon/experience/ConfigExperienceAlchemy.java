package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceAlchemy {

    private final static HashMap<String, Integer> ALCHEMY_DEFAULT_XP_MAP;

    static {
       ALCHEMY_DEFAULT_XP_MAP = new HashMap<>();
       ALCHEMY_DEFAULT_XP_MAP.put("Stage-One-Potion", 15);
       ALCHEMY_DEFAULT_XP_MAP.put("Stage-Two-Potion", 30);
       ALCHEMY_DEFAULT_XP_MAP.put("Stage-Three-Potion", 60);
       ALCHEMY_DEFAULT_XP_MAP.put("Stage-Four-Potion", 120);
    }

    @Setting(value = "Alchemy-Experience-Values", comment = "Experience values for alchemy.")
    HashMap<String, Integer> alchemyXPMap = ALCHEMY_DEFAULT_XP_MAP;


    public HashMap<String, Integer> getAlchemyXPMap() {
        return alchemyXPMap;
    }

}