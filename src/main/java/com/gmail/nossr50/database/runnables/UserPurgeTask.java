package com.gmail.nossr50.database.runnables;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.Database;
import com.gmail.nossr50.database.Leaderboard;

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
            Leaderboard.purgePowerlessFlatfile();

            if (Config.getInstance().getOldUsersCutoff() != -1) {
                Leaderboard.purgeOldFlatfile();
            }
        }
    }
}
