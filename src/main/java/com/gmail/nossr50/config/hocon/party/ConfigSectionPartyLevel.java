package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.datatypes.party.PartyFeature;
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

    @Setting(value = "Party-XP-Rate-Multiplier")
    private int partyXpCurveMultiplier = 3;

    @Setting(value = "Party-Leveling-Requires-Nearby-Party-Members")
    private boolean partyLevelingNeedsNearbyMembers = true;

    @Setting(value = "Send-Levelup-Notifications-To-Party")
    private boolean informPartyMembersOnLevelup = true;

    @Setting(value = "Party-Feature-Unlock-Level-Requirements")
    private Map<PartyFeature, Integer> partyFeatureUnlockMap = PARTY_FEATURE_MAP_DEFAULT;

    public static HashMap<PartyFeature, Integer> getPartyFeatureMapDefault() {
        return PARTY_FEATURE_MAP_DEFAULT;
    }

    public int getPartyXpCurveMultiplier() {
        return partyXpCurveMultiplier;
    }

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