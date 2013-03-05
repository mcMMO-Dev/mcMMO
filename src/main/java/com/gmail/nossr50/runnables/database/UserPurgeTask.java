package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.LeaderboardManager;

public class UserPurgeTask implements Runnable {
    @Override
    public void run() {
        if (Config.getInstance().getUseMySQL()) {
            DatabaseManager.purgePowerlessSQL();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                DatabaseManager.purgeOldSQL();
            }
        }
        else {
            LeaderboardManager.purgePowerlessFlatfile();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                LeaderboardManager.purgeOldFlatfile();
            }
        }
    }
}
