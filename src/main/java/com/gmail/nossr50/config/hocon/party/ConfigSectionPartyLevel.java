package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ConfigSectionPartyLevel {

    public static final boolean INFORM_PARTY_ON_LEVELUP_DEFAULT = true;
    public static final boolean PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT = true;
    public static final int TELEPORT_DEFAULT = 2;
    public static final int ALLIANCE_DEFAULT = 5;
    public static final int ITEM_SHARE_DEFAULT = 8;
    public static final int XP_SHARE_DEFAULT = 10;
    public static final int PARTY_CHAT_DEFAULT = 1;
    private static final HashMap<PartyFeature, Integer> PARTY_FEATURE_MAP_DEFAULT;

    static {
        PARTY_FEATURE_MAP_DEFAULT = new HashMap<>();
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.TELEPORT, TELEPORT_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.ALLIANCE, ALLIANCE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.ITEM_SHARE, ITEM_SHARE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.XP_SHARE, XP_SHARE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(PartyFeature.CHAT, PARTY_CHAT_DEFAULT);
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

    @Setting(value = "Party-Leveling-Requires-Nearby-Party-Members",
            comment = "If leveling your Party requires being near another party member." +
                    "\nDefault value: " + PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT)
    private boolean partyLevelingNeedsNearbyMembers = PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT;

    @Setting(value = "Send-Levelup-Notifications-To-Party",
            comment = "Sends level up notifications to nearby party members." +
                    "\nDefault value: " + INFORM_PARTY_ON_LEVELUP_DEFAULT)
    private boolean informPartyMembersOnLevelup = INFORM_PARTY_ON_LEVELUP_DEFAULT;

    @Setting(value = "Party-Feature-Unlock-Level-Requirements", comment = "What level your Party needs to be to unlock certain features." +
            "\nKeep in mind, parties no longer have a level cap." +
            "\n\nDefault values: " +
            "\nCHAT: " + PARTY_CHAT_DEFAULT +
            "\nTELEPORT: " + TELEPORT_DEFAULT +
            "\nALIANCE: " + ALLIANCE_DEFAULT +
            "\nITEM SHARE: " + ITEM_SHARE_DEFAULT +
            "\nXP SHARE: " + XP_SHARE_DEFAULT)
    private Map<PartyFeature, Integer> partyFeatureUnlockMap = PARTY_FEATURE_MAP_DEFAULT;

    public boolean isPartyLevelingNeedsNearbyMembers() {
        return partyLevelingNeedsNearbyMembers;
    }

    public boolean isInformPartyMembersOnLevelup() {
        return informPartyMembersOnLevelup;
    }

    public Map<PartyFeature, Integer> getPartyFeatureUnlockMap() {
        return partyFeatureUnlockMap;
    }
}