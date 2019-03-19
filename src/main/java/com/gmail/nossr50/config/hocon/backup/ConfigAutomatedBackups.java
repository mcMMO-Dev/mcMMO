package com.gmail.nossr50.config.hocon.backup;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAutomatedBackups {
    public static final int SAVE_INTERVAL_MINUTES_DEFAULT = 10;
    public static final boolean ZIP_BACKUPS_ENABLED_DEFAULT = true;
    public static final int BACKUP_DAY_LIMIT_DEFAULT = 30;
    //public static final int MINIMUM_BACKUP_COUNT_DEFAULT = 10;

    @Setting(value = "Database-Save-Interval-Minutes", comment = "How often mcMMO player data gets saved." +
            "\nThis value represents how many minutes in between saving mcMMO does." +
            "\nSaving is done in ASYNC threads, so it has almost no impact on performance, however you should not be saving too often as its a bit pointless and takes resources away from your machine." +
            "\nI recommend just leaving this at its default value." +
            "\nDefault value: "+SAVE_INTERVAL_MINUTES_DEFAULT)
    private int saveIntervalMinutes = SAVE_INTERVAL_MINUTES_DEFAULT;

    @Setting(value = "Backup-Configs-And-FlatFile-Data", comment = "mcMMO will make backups of your configs and other important data for you." +
            "\nNOTE: mcMMO will not be making backups of your SQL data, you will have to setup scripts for that yourself." +
            "\nmcMMO does not backup FlatFile user data if you are using SQL" +
            "\nYou can find the backups in the following directory inside your mcMMO folder - \\mcMMO\\backup" +
            "\nAutomated backups are deleted regularly once they reach a certain age, see the other options in this config to change this." +
            "\nBackups are made whenever you start your server for the first time and on each reload of the mcMMO plugin" +
            "\nDefault value: "+ZIP_BACKUPS_ENABLED_DEFAULT)
    private boolean zipBackupsEnabled = ZIP_BACKUPS_ENABLED_DEFAULT;

    @Setting(value = "Old-File-Age-Limit-In-Days", comment = "How many days should backups be kept in days?" +
            "\nBackups older than this are removed if the number of file backups is greater than \"Minimum-Backups\"." +
            "\nDefault value: "+ BACKUP_DAY_LIMIT_DEFAULT)
    private int backupDayLimit = BACKUP_DAY_LIMIT_DEFAULT;

/*    @Setting(value = "Minimum-Backups", comment = "The amount of backup files you must have before deletion of older files would be considered." +
            "\nThis does not prevent mcMMO from removing your old backups, it just prevents unnecessary removal of those files." +
            "\nFor example, if this value was set to 10. Then if you had 9 old backups from not having your server on for a long time," +
            "\n  then once the 10th backup is created those 9 older files would be removed." +
            "\nThis setting mostly prevents unnecessary operations rather than acting as a means to preserve a specific number of backups." +
            "\nDefault value: "+MINIMUM_BACKUP_COUNT_DEFAULT)
    private int minimumBackupCount = MINIMUM_BACKUP_COUNT_DEFAULT;*/

   /* public int getMinimumBackupCount() {
        return minimumBackupCount;
    }*/

    public int getSaveIntervalMinutes() {
        return saveIntervalMinutes;
    }

    public boolean isZipBackupsEnabled() {
        return zipBackupsEnabled;
    }

    public int getBackupDayLimit() {
        return backupDayLimit;
    }
}
