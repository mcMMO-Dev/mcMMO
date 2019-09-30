package com.gmail.nossr50.config.mobs;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionHealthBars {

    public static final boolean MOB_HEALTH_BARS_DEFAULT = true;
    public static final int DISPLAY_TIME_SECONDS_DEFAULT = 3;
    public static final String HEARTS = "HEARTS";
    public static final String displayTypesList = "\nYou can use the following MobHealthBarType values: HEARTS, BAR";

    @Setting(value = "Enable-Health-Bars", comment = "Turn this off to disable health bars appearing above mobs when damaged." +
            "\nDefault value: " + MOB_HEALTH_BARS_DEFAULT)
    private boolean enableHealthBars = MOB_HEALTH_BARS_DEFAULT;

    @Setting(value = "Display-Bar-Type", comment = "The type of display to use for the mobs health bar." +
            displayTypesList +
            "\nDefault value: " + HEARTS)
    private MobHealthbarType displayBarType = MobHealthbarType.HEARTS;

    @Setting(value = "Display-Time-In-Seconds", comment = "How many seconds mob health bars should be displayed before being hidden." +
            "\nDefault value: " + DISPLAY_TIME_SECONDS_DEFAULT)
    private int displayTimeSeconds = DISPLAY_TIME_SECONDS_DEFAULT;

    public boolean isEnableHealthBars() {
        return enableHealthBars;
    }

    public MobHealthbarType getDisplayBarType() {
        return displayBarType;
    }

    public int getDisplayTimeSeconds() {
        return displayTimeSeconds;
    }
}