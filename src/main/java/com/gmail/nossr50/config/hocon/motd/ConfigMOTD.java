package com.gmail.nossr50.config.hocon.motd;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMOTD {
    private static final boolean ENABLE_MOTD_DEFAULT = true;

    @Setting(value = "Show-MOTD-On-Player-Join", comment = "Show players who connect to the server the MOTD from mcMMO" +
            "\nHistorically this message is literally just telling players that the server runs mcMMO." +
            "\nSometimes the MOTD includes warnings about build stability if using a volatile dev build." +
            "\nIf you wish to edit the MOTD, that is done in the locale files inside the JAR." +
            "\nDefault value: "+ENABLE_MOTD_DEFAULT)
    private boolean enableMOTD = ENABLE_MOTD_DEFAULT;

    public boolean isEnableMOTD() {
        return enableMOTD;
    }
}
