package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceAcrobatics {

    private final static HashMap<String, Integer> ACROBATICS_DEFAULT_XP_MAP;
    private static final double FEATHER_FALL_MULTIPLIER_DEFAULT = 2.0D;

    static {
        ACROBATICS_DEFAULT_XP_MAP = new HashMap<>();
        ACROBATICS_DEFAULT_XP_MAP.put("Dodge", 800);
        ACROBATICS_DEFAULT_XP_MAP.put("Roll", 600);
        ACROBATICS_DEFAULT_XP_MAP.put("Fall", 600);
    }

    @Setting(value = "Acrobatics-Experience-Values", comment = "Experience values for Acrobatics.")
    HashMap<String, Integer> acrobaticsXPMap = ACROBATICS_DEFAULT_XP_MAP;

    @Setting(value = "Feather-Fall-XP-Multiplier", comment = "Feather Fall grants bonus XP to fall related XP gains." +
            "\nThis value is multiplied against your XP to give the bonus." +
            "\nDefault value: " + FEATHER_FALL_MULTIPLIER_DEFAULT)
    private Double featherFallMultiplier = FEATHER_FALL_MULTIPLIER_DEFAULT;

    public HashMap<String, Integer> getAcrobaticsXPMap() {
        return acrobaticsXPMap;
    }

    public Double getFeatherFallMultiplier() {
        return featherFallMultiplier;
    }

    public int getDodgeXP() {
        return acrobaticsXPMap.get("Dodge");
    }

    public int getRollXP() {
        return acrobaticsXPMap.get("Roll");
    }

    public int getFallXP() {
        return acrobaticsXPMap.get("Fall");
    }

}