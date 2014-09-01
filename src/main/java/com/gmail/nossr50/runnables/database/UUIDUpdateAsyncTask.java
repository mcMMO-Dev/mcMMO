package com.gmail.nossr50.runnables.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.uuid.UUIDService;

public class UUIDUpdateAsyncTask extends BukkitRunnable {

    private mcMMO plugin;
    private static final int MAX_LOOKUP = Math.max(HiddenConfig.getInstance().getUUIDConvertAmount(), 100);
    private static final int RATE_LIMIT = HiddenConfig.getInstance().getMojangRateLimit();
    private static final long LIMIT_PERIOD = HiddenConfig.getInstance().getMojangLimitPeriod();
    private static final int BATCH_SIZE = 5000;

    private List<String> userNames;
    private int size;
    private int checkedUsers;
    private int fetchedUsers;
    private long startMillis;

    public UUIDUpdateAsyncTask(mcMMO plugin, List<String> userNames) {
        this.plugin = plugin;
        this.userNames = userNames;

        this.checkedUsers = 0;
        this.fetchedUsers = 0;
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public void run() {
        size = userNames.size();
        UUIDService uuidService = null;

        plugin.getLogger().info("Starting to check and update UUIDs, total amount of users: " + size);

        List<String> userNamesSection;
        Map<String, UUID> fetchedUUIDs = new HashMap<String, UUID>();

        while (size != 0) {
            if (fetchedUsers + 100 > RATE_LIMIT) {
                try {
                    Thread.sleep(LIMIT_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                startMillis = System.currentTimeMillis();
                fetchedUsers = 0;
            }
            if (size > MAX_LOOKUP) {
                userNamesSection = userNames.subList(size - MAX_LOOKUP, size);
                size -= MAX_LOOKUP;
            } else {
                userNamesSection = userNames.subList(0, size);
                size = 0;
            }

            try {
                if (uuidService == null) uuidService = new UUIDService(userNamesSection);
                else uuidService.setList(userNamesSection);

                fetchedUUIDs.putAll(uuidService.call());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unable to fetch UUIDs!", e);
                return;
            }

            fetchedUsers += uuidService.getNumberFetched();
            checkedUsers += userNamesSection.size();
            userNamesSection.clear();
            size = userNames.size();

            Misc.printProgress(checkedUsers, DatabaseManager.progressInterval, startMillis);
            if (fetchedUUIDs.size() > BATCH_SIZE) {
                mcMMO.getDatabaseManager().saveUserUUIDs(fetchedUUIDs);
                fetchedUUIDs = new HashMap<String, UUID>();
            }
        }

        if (fetchedUUIDs.size() == 0 || mcMMO.getDatabaseManager().saveUserUUIDs(fetchedUUIDs)) {
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS);
            plugin.getLogger().info("UUID upgrade completed!");
        }
    }
}
