package com.gmail.nossr50.config.hocon.backup;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAutomatedBackups {
    public static final boolean ZIP_BACKUPS_ENABLED_DEFAULT = true;
    public static final int BACKUP_DAY_LIMIT_DEFAULT = 30;

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

    public boolean isZipBackupsEnabled() {
        return zipBackupsEnabled;
    }

    public int getBackupDayLimit() {
        return backupDayLimit;
    }
}
