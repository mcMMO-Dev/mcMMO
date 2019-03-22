package com.gmail.nossr50.config.hocon.skills;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSubSkillScalingRNG {

    @Setting(value = "Standard-Mode-Scaling-Settings", comment = "Standard mode is the new default level scaling for mcMMO" +
            "\nMost skills in standard mode scale from 1-100, maxing out at 100." +
            "\nStandard scaling is fairly new, and it replaced the previous scaling method which is now known as RetroMode scaling." +
            "\nYou are either using Standard or Retro mode on your server, which one you are using is setup in the leveling config file." +
            "\nSettings from here are only applied when using Standard mode scaling.")
    private ConfigScalingSubSkillStandard standardSettings = new ConfigScalingSubSkillStandard();

    @Setting(value = "Retro-Mode-Scaling-Settings", comment = "Retro mode is the optional level scaling for mcMMO, which was replaced by Standard scaling." +
            "\nMost skills in retro mode scale from 1-1000, maxing out at 1000." +
            "\nRetro scaling was the main method of scaling in mcMMO for almost 8 years," +
            "\n    and it was replaced in 2.1 with the new 1-100 scaling method which is known as Standard mode scaling." +
            "\nYou can still use Retro Mode scaling, it will never be removed from mcMMO so do not worry about using it!" +
            "\nYou are either using Standard or Retro mode on your server, which one you are using is setup in the leveling config file." +
            "\nSettings from here are only applied when using Retro mode scaling.")
    private ConfigScalingSubSkillRetro retroSettings = new ConfigScalingSubSkillRetro();


    public ConfigScalingSubSkillStandard getStandardSettings() {
        return standardSettings;
    }

    public ConfigScalingSubSkillRetro getRetroSettings() {
        return retroSettings;
    }
}