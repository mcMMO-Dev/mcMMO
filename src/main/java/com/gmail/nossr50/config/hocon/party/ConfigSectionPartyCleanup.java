package com.gmail.nossr50.config.hocon.party;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyCleanup {

    private static final int AUTO_KICK_HOURS_DEFAULT = 12;
    public static final int AUTO_KICK_CUTOFF_DAYS_DEFAULT = 7;

    @Setting(value = "Hours-Between-Cleanup-Operations",
            comment = "How many hours between checking parties for members that meet auto kick requirements." +
                    "\nDefault value: "+AUTO_KICK_HOURS_DEFAULT)
    private int partyAutoKickHoursInterval = AUTO_KICK_HOURS_DEFAULT;

    @Setting(value = "Offline-Day-Limit",
            comment = "How many days must pass before a player qualifies to be kicked from a party automatically." +
                    "\nDefault value: "+AUTO_KICK_CUTOFF_DAYS_DEFAULT)
    private int partyAutoKickDaysCutoff = AUTO_KICK_CUTOFF_DAYS_DEFAULT;

    public int getPartyAutoKickHoursInterval() {
        return partyAutoKickHoursInterval;
    }

    public int getPartyAutoKickDaysCutoff() {
        return partyAutoKickDaysCutoff;
    }
}