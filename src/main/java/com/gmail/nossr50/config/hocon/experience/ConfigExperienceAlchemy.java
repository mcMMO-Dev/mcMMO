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

    public int getStageOnePotionXP() {
        return alchemyXPMap.get("Stage-One-Potion");
    }

    public int getStageTwoPotionXP() {
        return alchemyXPMap.get("Stage-Two-Potion");
    }

    public int getStageThreePotionXP() {
        return alchemyXPMap.get("Stage-Three-Potion");
    }

    public int getStageFourPotionXP() {
        return alchemyXPMap.get("Stage-Four-Potion");
    }

    /*public int getStageFivePotionXP()
    {
        //This is purposely zero to prevent an exploit
        return 0;
    }*/

    public int getPotionXPByStage(int potionStage) {
        switch (potionStage) {
            case 1:
                return getStageOnePotionXP();
            case 2:
                return getStageTwoPotionXP();
            case 3:
                return getStageThreePotionXP();
            case 4:
                return getStageFourPotionXP();
            default:
                return 0; //Zero XP is intentional to prevent some infinite loop XP exploit
        }
    }

}