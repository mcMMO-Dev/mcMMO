package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.config.hocon.HOCONUtil;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ConfigSectionPartyLevel {

    private static final HashMap<String, Integer> PARTY_FEATURE_MAP_DEFAULT;
    public static final boolean INFORM_PARTY_ON_LEVELUP_DEFAULT = true;

    public static final boolean PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT = true;

    public static final int TELEPORT_DEFAULT = 2;

    public static final int ALLIANCE_DEFAULT = 5;

    public static final int ITEM_SHARE_DEFAULT = 8;

    public static final int XP_SHARE_DEFAULT = 10;

    public static final int PARTY_CHAT_DEFAULT = 1;

    static {
        PARTY_FEATURE_MAP_DEFAULT = new HashMap<>();
        PARTY_FEATURE_MAP_DEFAULT.put(HOCONUtil.serializeENUMName(PartyFeature.TELEPORT.toString()), TELEPORT_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(HOCONUtil.serializeENUMName(PartyFeature.ALLIANCE.toString()), ALLIANCE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(HOCONUtil.serializeENUMName(PartyFeature.ITEM_SHARE.toString()), ITEM_SHARE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(HOCONUtil.serializeENUMName(PartyFeature.XP_SHARE.toString()), XP_SHARE_DEFAULT);
        PARTY_FEATURE_MAP_DEFAULT.put(HOCONUtil.serializeENUMName(PartyFeature.CHAT.toString()), PARTY_CHAT_DEFAULT);
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

    @Setting(value = "Party-XP-Formula-Parameters",
            comment =   "The Party XP Formula is borrowed from the Player XP formula to help determine the amount of XP needed to level the party." +
                        "\nThe Party XP Formula used to be based on your settings for player XP formula but I have separated it from those settings." +
                        "\nThe Party XP Curve Multiplier takes the final result of calculating one level of XP and multiplies it by this value to get the amount of XP needed to level the party." +
                        "\nParty Leveling used to have a level cap, I have removed this level cap as part of a feature request. It seems fun to level up parties indefinitely." +
                        "\nParty Leveling is now using exponential level scaling by default.")
    private ConfigSectionPartyXPFormula partyXPFormula = new ConfigSectionPartyXPFormula();

    @Setting(value = "Party-Leveling-Requires-Nearby-Party-Members",
            comment = "If leveling your Party requires being near another party member." +
                    "\nDefault value: "+PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT)
    private boolean partyLevelingNeedsNearbyMembers = PARTY_LEVELING_NEEDS_NERBY_MEMBERS_DEFAULT;

    @Setting(value = "Send-Levelup-Notifications-To-Party",
            comment = "Sends level up notifications to nearby party members." +
            "\nDefault value: "+INFORM_PARTY_ON_LEVELUP_DEFAULT)
    private boolean informPartyMembersOnLevelup = INFORM_PARTY_ON_LEVELUP_DEFAULT;

    @Setting(value = "Party-Feature-Unlock-Level-Requirements", comment = "What level your Party needs to be to unlock certain features." +
            "\nKeep in mind, parties no longer have a level cap." +
            "\n\nDefault values: " +
            "\nCHAT: "+PARTY_CHAT_DEFAULT +
            "\nTELEPORT: "+TELEPORT_DEFAULT +
            "\nALIANCE: "+ALLIANCE_DEFAULT +
            "\nITEM SHARE: "+ITEM_SHARE_DEFAULT +
            "\nXP SHARE: "+XP_SHARE_DEFAULT)
    private Map<String, Integer> partyFeatureUnlockMap = PARTY_FEATURE_MAP_DEFAULT;



    public int getPartyXpCurveMultiplier() {
        return partyXPFormula.getPartyXpCurveMultiplier();
    }

    public boolean isPartyLevelingNeedsNearbyMembers() {
        return partyLevelingNeedsNearbyMembers;
    }

    public boolean isInformPartyMembersOnLevelup() {
        return informPartyMembersOnLevelup;
    }

    public Map<String, Integer> getPartyFeatureUnlockMap() { return partyFeatureUnlockMap; }
}