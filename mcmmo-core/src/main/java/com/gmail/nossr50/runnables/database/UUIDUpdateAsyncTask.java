package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.uuid.UUIDFetcher;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class UUIDUpdateAsyncTask extends BukkitRunnable {
    private mcMMO pluginRef;
    private final int MAX_LOOKUP = 100;
    private final int RATE_LIMIT = 300;
    private final long LIMIT_PERIOD = 6000;
    private final int BATCH_SIZE = MAX_LOOKUP * 3;

    private List<String> userNames;
    private int size;
    private int checkedUsers;
    private long startMillis;

    public UUIDUpdateAsyncTask(mcMMO pluginRef, List<String> userNames) {
        this.pluginRef = pluginRef;
        this.userNames = userNames;

        this.checkedUsers = 0;
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public void run() {
        size = userNames.size();

        pluginRef.getLogger().info("Starting to check and update UUIDs, total amount of users: " + size);

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
                if (e.getMessage() != null) {
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
                }

                pluginRef.getLogger().log(Level.SEVERE, "Unable to fetch UUIDs!", e);
                return;
            }

            checkedUsers += userNamesSection.size();
            userNamesSection.clear();
            size = userNames.size();

            pluginRef.getDatabaseManager().printProgress(checkedUsers, startMillis, pluginRef.getLogger());

            if (fetchedUUIDs.size() >= BATCH_SIZE) {
                pluginRef.getDatabaseManager().saveUserUUIDs(fetchedUUIDs);
                fetchedUUIDs = new HashMap<String, UUID>();
            }
        }

        if (fetchedUUIDs.size() == 0 || pluginRef.getDatabaseManager().saveUserUUIDs(fetchedUUIDs)) {
            //mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS);
            pluginRef.getLogger().info("UUID upgrade completed!");
        }
    }
}