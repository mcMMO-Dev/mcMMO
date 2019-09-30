package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyExperienceSharing {

    @Setting(value = "XP-Share-Base")
    private double partyShareXPBonusBase = 1.1D;

    @Setting(value = "XP-Share-Increase")
    private double partyShareBonusIncrease = 1.05D;

    @Setting(value = "XP-Share-Cap")
    private double partyShareBonusCap = 1.5D;

    @Setting(value = "XP-Share-Range",
            comment = "How far away you can be from a party member and still receive shared XP.")
    private double partyShareRange = 75.0D;

    public double getPartyShareXPBonusBase() {
        return partyShareXPBonusBase;
    }

    public double getPartyShareBonusIncrease() {
        return partyShareBonusIncrease;
    }

    public double getPartyShareBonusCap() {
        return partyShareBonusCap;
    }

    public double getPartyShareRange() {
        return partyShareRange;
    }
}