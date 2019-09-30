package com.gmail.nossr50.config.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionDatabaseGeneral {

    public static final int SAVE_INTERVAL_MINUTES_DEFAULT = 10;

    @Setting(value = "Save-Interval-Minutes", comment = "How often the database will save." +
            "\nSaving the database is an expensive operation although it is done in an ASYNC thread." +
            "\nI wouldn't recommend setting this value lower than 10 minutes" +
            "\nKeep in mind if you properly shut down your server with a stop command mcMMO saves before your server shuts down." +
            "\nDefault value: " + SAVE_INTERVAL_MINUTES_DEFAULT)
    private int saveIntervalMinutes = SAVE_INTERVAL_MINUTES_DEFAULT;

    public int getSaveIntervalMinutes() {
        return saveIntervalMinutes;
    }
}