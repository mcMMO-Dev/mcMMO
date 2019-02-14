package com.gmail.nossr50.core.runnables.database;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.config.ChunkConversionOptions;
import com.gmail.nossr50.core.data.database.DatabaseManager;
import com.gmail.nossr50.core.datatypes.database.UpgradeType;
import com.gmail.nossr50.core.util.Misc;
import com.gmail.nossr50.core.util.uuid.UUIDFetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class UUIDUpdateAsyncTask implements Runnable {
    private static final int MAX_LOOKUP = Math.max(ChunkConversionOptions.getUUIDConvertAmount(), 100);
    private static final int RATE_LIMIT = ChunkConversionOptions.getMojangRateLimit();
    private static final long LIMIT_PERIOD = ChunkConversionOptions.getMojangLimitPeriod();
    private static final int BATCH_SIZE = MAX_LOOKUP * 3;
    private List<String> userNames;
    private int size;
    private int checkedUsers;
    private long startMillis;

    public UUIDUpdateAsyncTask(List<String> userNames) {
        this.userNames = userNames;

        this.checkedUsers = 0;
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public void run() {
        size = userNames.size();

        McmmoCore.getLogger().info("Starting to check and update UUIDs, total amount of users: " + size);

        List<String> userNamesSection;
        Map<String, UUID> fetchedUUIDs = new HashMap<String, UUID>();

        while (size != 0) {
            if (checkedUsers + 100 > RATE_LIMIT) {
                try {
                    Thread.sleep(LIMIT_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                startMillis = System.currentTimeMillis();
                checkedUsers = 0;
            }
            if (size > MAX_LOOKUP) {
                userNamesSection = userNames.subList(size - MAX_LOOKUP, size);
                size -= MAX_LOOKUP;
            } else {
                userNamesSection = userNames.subList(0, size);
                size = 0;
            }

            try {
                fetchedUUIDs.putAll(new UUIDFetcher(userNamesSection).call());
            } catch (Exception e) {
                // Handle 429
                if (e.getMessage().contains("429")) {
                    size += userNamesSection.size();
                    try {
                        Thread.sleep(LIMIT_PERIOD);
                    } catch (InterruptedException ex) {
                        e.printStackTrace();
                        return;
                    }
                    continue;
                }

                McmmoCore.getLogger().log(Level.SEVERE, "Unable to fetch UUIDs!", e);
                return;
            }

            checkedUsers += userNamesSection.size();
            userNamesSection.clear();
            size = userNames.size();

            Misc.printProgress(checkedUsers, DatabaseManager.progressInterval, startMillis);
            if (fetchedUUIDs.size() >= BATCH_SIZE) {
                McmmoCore.getDatabaseManager().saveUserUUIDs(fetchedUUIDs);
                fetchedUUIDs = new HashMap<String, UUID>();
            }
        }

        if (fetchedUUIDs.size() == 0 || McmmoCore.getDatabaseManager().saveUserUUIDs(fetchedUUIDs)) {
            McmmoCore.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS);
            McmmoCore.getLogger().info("UUID upgrade completed!");
        }
    }
}
