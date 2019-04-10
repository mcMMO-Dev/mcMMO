package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceAcrobatics {

    private final static HashMap<String, Integer> ACROBATICS_DEFAULT_XP_MAP;

    static {
        ACROBATICS_DEFAULT_XP_MAP = new HashMap<>();

    }

    @Setting(value = "Acrobatics-Experience-Values", comment = "Experience values for Acrobatics.")
    HashMap<String, Integer> acrobaticsXPMap;

}