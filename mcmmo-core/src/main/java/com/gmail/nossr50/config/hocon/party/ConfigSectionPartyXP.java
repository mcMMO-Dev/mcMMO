package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyXP {

    @Setting(value = "Party-Experience-Sharing", comment = "Settings for party XP sharing." +
            "\nThe formula for determining shared XP is like this..." +
            "\n x = XP-Share-Base" +
            "\n y = Number of Party members in XP share range" +
            "\n z = XP-Share-Increase" +
            "\n shareBonus = (y * z) + x" +
            "\nNOTE: shareBonus will never be bigger than XP-Share-Cap" +
            "\n xpGained = Amount of XP gained before being split" +
            "\n\n SHARED_XP = (xpGained / y * shareBonus)" +
            "\nSHARED_XP is what will be given to nearby party members." +
            "\n\nKeep in mind, if you gain XP in say Acrobatics and then that XP is shared with your party members, " +
            "that doesn't mean that you will get extra XP from the XP sharing.")
    private ConfigSectionPartyExperienceSharing partyExperienceSharing = new ConfigSectionPartyExperienceSharing();

    @Setting(value = "Party-Level", comment = "Parties in mcMMO gain levels just like skills" +
            "\nSettings related to that can be found here!.")
    private ConfigSectionPartyLevel partyLevel = new ConfigSectionPartyLevel();

    public ConfigSectionPartyExperienceSharing getPartyExperienceSharing() {
        return partyExperienceSharing;
    }

    public ConfigSectionPartyLevel getPartyLevel() {
        return partyLevel;
    }
}