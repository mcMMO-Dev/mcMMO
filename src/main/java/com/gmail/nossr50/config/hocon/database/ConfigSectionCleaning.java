package com.gmail.nossr50.config.hocon.database;

import com.gmail.nossr50.config.ConfigConstants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSectionCleaning {

    /* DEFAULT VALUES */
    private static final boolean PURGE_OLD_USERS = false;
    private static final boolean PURGE_POWERLESS_USERS = true;
    private static final boolean ONLY_PURGE_AT_STARTUP = false;
    private static final int PURGE_INTERVAL_DEFAULT = 1;
    private static final int OLD_USER_CUTOFF_IN_MONTHS = 6;

    /*
     * CONFIG NODES
     */

    @Setting(value = "Purge-Old-Users",
            comment = "Turn this on to enable automatic database pruning of old users." +
                    "\nDefault value: "+PURGE_OLD_USERS)
    private boolean purgeOldUsers = PURGE_OLD_USERS;

    @Setting(value = "Purge-Powerless-Users", comment = "Powerless users are players who have not" +
            " leveled up in a single skill." +
            "\nDefault value: "+PURGE_POWERLESS_USERS)
    private boolean purgePowerlessUsers = PURGE_POWERLESS_USERS;

    @Setting(value = "Only-Purge-At-Plugin-Startup",
            comment = "If set to true, then purging will only happen when the plugin first loads." +
                    "\nKeep in mind, this will trigger on reload as well." +
                    "\nThis purge is on a 2 second delay from plugin start-up and runs in an ASYNC thread." +
                    "\nDefault value: "+ONLY_PURGE_AT_STARTUP)
    private boolean onlyPurgeAtStartup = ONLY_PURGE_AT_STARTUP;

    @Setting(value = "Purge-Interval-In-Hours", comment = "How many hours between automatic purging?")
    private int purgeInterval = PURGE_INTERVAL_DEFAULT;

    @Setting(value = "Old-User-Cutoff-In-Months", comment = "Users who haven't connected in this many months will be purged" +
            "\nDefault value: "+OLD_USER_CUTOFF_IN_MONTHS)
    private int oldUserCutoffMonths = OLD_USER_CUTOFF_IN_MONTHS;

    /*
     * GETTER BOILERPLATE
     */

    public boolean isPurgePowerlessUsers() {
        return purgePowerlessUsers;
    }

    public boolean isPurgeOldUsers() {
        return purgeOldUsers;
    }

    public boolean isOnlyPurgeAtStartup() {
        return onlyPurgeAtStartup;
    }

    public int getPurgeInterval() {
        return purgeInterval;
    }

    public int getOldUserCutoffMonths() {
        return oldUserCutoffMonths;
    }
}
