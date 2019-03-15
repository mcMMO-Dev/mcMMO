package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyCombat {

    public static final boolean PARTY_FRIENDLY_FIRE_DEFAULT = false;

    @Setting(value = "Friendly-Fire", comment = "When friendly fire is enabled, players in the same party can injure each other.")
    private boolean partyFriendlyFire = PARTY_FRIENDLY_FIRE_DEFAULT;

    public boolean isPartyFriendlyFire() {
        return partyFriendlyFire;
    }
}
