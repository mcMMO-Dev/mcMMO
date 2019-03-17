package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ConfigSectionPartyLevel {

    private static final HashMap<PartyFeature, Integer> PARTY_FEATURE_MAP_DEFAULT;

    static {
        PARTY_FEATURE_MAP_DEFAULT = new HashMap<>();
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.TELEPORT, 2);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.ALLIANCE, 5);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.ITEM_SHARE, 8);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.XP_SHARE, 10);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.CHAT, 1);
    }

    /*
        int base = ExperienceConfig.getInstance().getBase(FormulaType.EXPONENTIAL);
        double multiplier = ExperienceConfig.getInstance().getMultiplier(FormulaType.EXPONENTIAL);
        double exponent = ExperienceConfig.getInstance().getExponent(FormulaType.EXPONENTIAL);

        if (!experienceNeededExponential.containsKey(level)) {
            experience = (int) Math.floor((multiplier * Math.pow(level, exponent) + base));
            experience *= mcMMO.getConfigManager().getConfigParty().getPartyXP().getPartyLevel().getPartyXpCurveMultiplier();
            experienceNeededExponential.put(level, experience);
        }
     */

    @Setting(value = "Party-XP-Formula-Multiplier",
            comment = "The Party XP Formula is borrowed from the Player XP formula to help determine the amount of XP needed to level the party." +
                    "\nThe Party XP Formula used to be based on your settings for player XP formula but I have separated it from those settings." +
                    "\nThe Party XP Curve Multiplier takes the final result of calculating one level of XP and multiplies it by this value to get the amount of XP needed to level the party." +
                    "\nParty Leveling used to have a level cap, I have removed this level cap as part of a feature request. It seems fun to level up parties indefinitely." +
                    "\nParty Leveling is now using exponential level scaling by default.")
    private int partyXpCurveMultiplier = 10;

    @Setting(value = "Party-Leveling-Requires-Nearby-Party-Members")
    private boolean partyLevelingNeedsNearbyMembers = true;

    @Setting(value = "Send-Levelup-Notifications-To-Party")
    private boolean informPartyMembersOnLevelup = true;

    @Setting(value = "Party-Feature-Unlock-Level-Requirements")
    private Map<PartyFeature, Integer> partyFeatureUnlockMap = PARTY_FEATURE_MAP_DEFAULT;

    public int getPartyXpCurveMultiplier() {
        return partyXpCurveMultiplier;
    }

    public boolean isPartyLevelingNeedsNearbyMembers() {
        return partyLevelingNeedsNearbyMembers;
    }

    public boolean isInformPartyMembersOnLevelup() {
        return informPartyMembersOnLevelup;
    }

    public HashMap<PartyFeature, Integer> getPartyFeatureUnlockMap() {
        return Maps.newHashMap(partyFeatureUnlockMap);
    }
}