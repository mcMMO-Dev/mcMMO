package com.gmail.nossr50.config.hocon.party;

import com.gmail.nossr50.datatypes.party.ShareMode;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionPartyItemShareSettings {

    public static final ShareMode SHARE_MODE_DEFAULT = ShareMode.NONE;

    @Setting(value = "Item-Share-Distribution-Model", comment = "Determines how to distribute dropped items between party members." +
            "\nEQUAL: Party members have weighted dice rolls based on the quality of the loot they have recently received." +
            "\nRANDOM: Party members do a fair dice roll for every item dropped." +
            "\nNONE: Do not use item sharing" +
            "\nDefault Value: "+"NONE")
    private ShareMode shareMode = SHARE_MODE_DEFAULT;
}