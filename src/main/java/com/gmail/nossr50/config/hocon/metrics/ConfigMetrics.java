package com.gmail.nossr50.config.hocon.metrics;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigMetrics {

    public static final boolean ALLOW_STAT_TRACKING_DEFAULT = true;

    @Setting(value = "Allow-Anonymous-Statistic-Collection", comment = "Collects info about what version of mcMMO you are using and other information" +
            "\nAll information is completely anonymous, and that info is reported to bstats for data processing." +
            "\nThis setting should have no affect on your server whatsoever, so I'd like to discourage you from turning it off." +
            "\nDefault value: "+ALLOW_STAT_TRACKING_DEFAULT)
    private boolean allowAnonymousUsageStatistics = ALLOW_STAT_TRACKING_DEFAULT;

    public boolean isAllowAnonymousUsageStatistics() {
        return allowAnonymousUsageStatistics;
    }
}
