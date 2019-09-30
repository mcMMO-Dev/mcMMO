package com.gmail.nossr50.config.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigDatabaseFlatFile {

    public static final int LEADERBOARD_SCOREBOARD_UPDATE_INTERVAL_MINUTES_DEFAULT = 10;

    @Setting(value = "Scoreboard-Leaderboard-Update-Interval", comment = "How often the scoreboard leaderboards will update." +
            "\nThis is an expensive operation, it is highly recommended to avoid doing this often." +
            "\nDefault value: " + LEADERBOARD_SCOREBOARD_UPDATE_INTERVAL_MINUTES_DEFAULT)
    private int leaderboardUpdateIntervalMinutes = LEADERBOARD_SCOREBOARD_UPDATE_INTERVAL_MINUTES_DEFAULT;

    public int getLeaderboardUpdateIntervalMinutes() {
        return leaderboardUpdateIntervalMinutes;
    }

}