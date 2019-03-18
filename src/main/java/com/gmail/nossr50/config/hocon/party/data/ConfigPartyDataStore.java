package com.gmail.nossr50.config.hocon.party.data;

import com.gmail.nossr50.datatypes.party.ShareMode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.UUID;

@ConfigSerializable
public class ConfigPartyDataStore {
    @Setting(value = "Party-Leader")
    private UUID partyLeader;

    @Setting(value = "Party-Members")
    private ArrayList<UUID> partyMembers;

    @Setting(value = "Party-Name")
    private String partyName;

    @Setting(value = "Party-XP-Share-Mode")
    private ShareMode partyXPShareMode;

    @Setting(value = "Party-Level")
    private int partyLevel;

    @Setting(value = "Party-XP")
    private int partyXP;

    @Setting(value = "Party-Locked")
    private boolean partyLocked;

    @Setting(value = "Party-Password")
    private String partyPassword;

    @Setting(value = "Party-Item-Share-Mode")
    private ShareMode partyItemShareMode;
}