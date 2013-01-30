package com.gmail.nossr50.database.runnables;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;

public class UserPurgeTask implements Runnable {
    @Override
    public void run() {
        if (Config.getInstance().getUseMySQL()) {
            Database.purgePowerlessSQL();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                Database.purgeOldSQL();
            }
        }
        else {
            //TODO: Make this work for Flatfile data.
        }
    }
}
