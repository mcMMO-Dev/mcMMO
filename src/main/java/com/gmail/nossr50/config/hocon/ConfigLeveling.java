package com.gmail.nossr50.config.hocon;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigLeveling {

    private static final int STARTING_LEVEL_DEFAULT = 1;

    @Setting(value = "Player_Starting_Level",
            comment = "Players will start at this level in all skills if they aren't already saved in the database." +
                    "\nHistorically this number has been 0, but this was changed in 2.1.X to 1 as I felt it was better to start from 1 than 0." +
                    "\nDefault value: "+STARTING_LEVEL_DEFAULT)
    private int startingLevel = STARTING_LEVEL_DEFAULT;

    public int getStartingLevel() {
        return startingLevel;
    }
}
