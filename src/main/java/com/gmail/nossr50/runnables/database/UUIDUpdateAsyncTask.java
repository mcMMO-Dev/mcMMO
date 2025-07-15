package com.gmail.nossr50.runnables.database;

import com.gmail.nossr50.datatypes.database.UpgradeType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.Misc;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class UUIDUpdateAsyncTask extends CancellableRunnable {
    private static final Gson GSON = new Gson();
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

    private static final int HARD_LIMIT_PERIOD = 600; // Mojang rate limit period is 600 seconds, wait that long if they reject us
    private static final int RETRY_PERIOD = 60; // Wait 60 seconds on connection failure
    private static final int DELAY_PERIOD = 100; // Wait 100 seconds between each batch

    private static final int BATCH_SIZE = 100; // 100 at a time

    private final CountDownLatch awaiter = new CountDownLatch(1);
    private final mcMMO plugin;
    private final ImmutableList<String> userNames;
    private int position = 0;

    public UUIDUpdateAsyncTask(mcMMO plugin, List<String> userNames) {
        this.plugin = plugin;
        this.userNames = ImmutableList.copyOf(userNames);
    }

    @Override
    public void run() {
        // First iteration
        if (position == 0) {
            plugin.getLogger().info("Starting to check and update UUIDs, total amount of users: "
                    + userNames.size());
        }

        List<String> batch = userNames.subList(position,
                Math.min(userNames.size(), position + BATCH_SIZE));

        Map<String, UUID> fetchedUUIDs = new HashMap<>();
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(PROFILE_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String body = GSON.toJson(batch.toArray(new String[0]));

            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes());
                output.flush();
            }
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    break; // All is good
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_FORBIDDEN:
                    // Rejected, probably rate limit, just wait it out
                    this.runTaskLaterAsynchronously(plugin,
                            Misc.TICK_CONVERSION_FACTOR * HARD_LIMIT_PERIOD);
                    return;
                default:
                    // Unknown failure
                    this.runTaskLaterAsynchronously(plugin,
                            Misc.TICK_CONVERSION_FACTOR * RETRY_PERIOD);
                    return;
            }

            try (InputStream input = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(input)) {
                for (JsonObject jsonProfile : GSON.fromJson(reader, JsonObject[].class)) {
                    UUID id = toUUID(jsonProfile.get("id").getAsString());
                    String name = jsonProfile.get("name").getAsString();
                    fetchedUUIDs.put(name, id);
                }
            }
        } catch (IOException e) {
            // failure
            plugin.getLogger().log(Level.SEVERE, "Unable to contact mojang API!", e);
            this.runTaskLaterAsynchronously(plugin, Misc.TICK_CONVERSION_FACTOR * RETRY_PERIOD);
            return;
        }

        if (fetchedUUIDs.size() != 0) {
            mcMMO.getDatabaseManager().saveUserUUIDs(fetchedUUIDs);
        }

        position += batch.size();
        plugin.getLogger().info(String.format("Conversion progress: %d/%d users", position,
                userNames.size()));

        if (position + 1 >= userNames.size()) {
            mcMMO.getUpgradeManager().setUpgradeCompleted(UpgradeType.ADD_UUIDS);
            awaiter.countDown();
            plugin.getLogger().info("UUID checks completed");
        } else {
            this.runTaskLaterAsynchronously(plugin,
                    Misc.TICK_CONVERSION_FACTOR * DELAY_PERIOD); // Schedule next batch
        }
    }

    // Bukkit runnables don't let themselves reschedule themselves, so we are a pseudo bukkit runnable.
    private void runTaskLaterAsynchronously(mcMMO plugin, int delay) {
        plugin.getFoliaLib().getScheduler().runLaterAsync(this, delay);
    }

    public void start() {
        plugin.getFoliaLib().getScheduler().runAsync(this);
    }

    private static UUID toUUID(String id) {
        return UUID.fromString(
                id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-"
                        + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public void waitUntilFinished() {
        try {
            awaiter.await();
        } catch (InterruptedException e) {
            // I guess we don't care in this event
        }
    }
}
