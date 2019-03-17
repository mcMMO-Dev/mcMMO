package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyXPFormula {

    public static final int PARTY_XP_CURVE_MULTIPLIER_DEFAULT = 10;

    @Setting(value = "Party-XP-Formula-Multiplier",
            comment = "Crank this up to make it harder to level parties" +
                    "\nDefault value: "+PARTY_XP_CURVE_MULTIPLIER_DEFAULT)
    private int partyXpCurveMultiplier = PARTY_XP_CURVE_MULTIPLIER_DEFAULT;

    public int getPartyXpCurveMultiplier() {
        return partyXpCurveMultiplier;
    }
}