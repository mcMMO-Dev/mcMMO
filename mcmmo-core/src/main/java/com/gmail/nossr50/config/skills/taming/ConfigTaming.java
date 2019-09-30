package com.gmail.nossr50.config.skills.taming;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigTaming {

    @Setting(value = ConfigConstants.SUB_SKILL_NODE)
    private ConfigTamingSubSkills subSkills = new ConfigTamingSubSkills();

    public ConfigTamingSubSkills getSubSkills() {
        return subSkills;
    }

    public ConfigTamingGore getGore() {
        return subSkills.getGore();
    }

    public ConfigTamingCallOfTheWild getCallOfTheWild() {
        return subSkills.getCallOfTheWild();
    }

    public ConfigTamingSharpenedClaws getSharpenedClaws() {
        return subSkills.getSharpenedClaws();
    }

    public ConfigTamingShockProof getShockProof() {
        return subSkills.getShockProof();
    }

    public ConfigTamingThickFur getThickFur() {
        return subSkills.getThickFur();
    }

    public ConfigTamingEnvironmentallyAware getEnvironmentallyAware() {
        return subSkills.getEnvironmentallyAware();
    }

    public ConfigTamingFastFoodService getFastFoodService() {
        return subSkills.getFastFoodService();
    }

    public ConfigTamingPummel getPummel() {
        return subSkills.getPummel();
    }

    public double getMinHorseJumpStrength() {
        return subSkills.getMinHorseJumpStrength();
    }

    public double getMaxHorseJumpStrength() {
        return subSkills.getMaxHorseJumpStrength();
    }
}