package com.gmail.nossr50.runnables;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Database;

public class UserPurgeTask implements Runnable {
    private Database database = mcMMO.getPlayerDatabase();

    public UserPurgeTask() {
        
    }

    @Override
    public void run() {
        if (Config.getInstance().getUseMySQL()) {
            database.purgePowerlessSQL();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                database.purgeOldSQL();
            }
        }
        else {
            //TODO: Make this work for Flatfile data.
        }
    }
}
