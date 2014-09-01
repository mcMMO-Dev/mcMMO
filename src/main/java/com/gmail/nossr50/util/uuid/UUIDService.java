package com.gmail.nossr50.util.uuid;

import com.gmail.nossr50.config.Config;
import com.google.common.collect.ImmutableList;
import com.turt2live.uuid.CachingServiceProvider;
import com.turt2live.uuid.PlayerRecord;
import com.turt2live.uuid.ServiceProvider;
import com.turt2live.uuid.turt2live.v2.ApiV2Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service layer for UUIDs
 *
 * @author turt2live
 */
public class UUIDService {

    private static final int SERVICE_LIMIT_PER_REQUEST = 100;

    private static ServiceProvider UUID_PROVIDER;

    private List<String> names;
    private int numFetched = 0; // From last request

    /**
     * Creates a new UUID service that takes a list of usernames
     * to lookup via {@link #call()}
     *
     * @param names the names to lookup
     */
    public UUIDService(List<String> names) {
        this.names = ImmutableList.copyOf(names);
    }

    /**
     * Sets a new list of names to parse
     *
     * @param names the names to lookup
     */
    public void setList(List<String> names) {
        this.names = ImmutableList.copyOf(names);
    }

    /**
     * Parses the predefined list of names to a UUID map
     *
     * @return the generated map
     */
    public Map<String, UUID> call() throws Exception {
        int fetched = 0;
        Map<String, UUID> map = new HashMap<String, UUID>();

        if (Config.getInstance().getUseUUIDWebCache()) {
            int requests = (int) Math.ceil(names.size() / SERVICE_LIMIT_PER_REQUEST);
            for (int i = 0; i < requests; i++) {
                List<String> subNames = names.subList(i * SERVICE_LIMIT_PER_REQUEST, Math.min((i + 1) * SERVICE_LIMIT_PER_REQUEST, names.size()));
                List<PlayerRecord> records = UUID_PROVIDER.doBulkLookup(subNames.toArray(new String[subNames.size()]));

                // List only includes successful lookups
                for (PlayerRecord record : records) {
                    map.put(record.getName(), record.getUuid());
                    if (!record.isCached()) fetched++;
                }
            }
        } else {
            map = new UUIDFetcher(names).call();
            fetched = map.size();
        }

        numFetched = fetched;
        return map;
    }

    /**
     * Gets the number of profiles that were actually fetched from the last
     * call to {@link #call()}.
     *
     * @return the number of profiles actually fetched
     */
    public int getNumberFetched() {
        return numFetched;
    }

    /**
     * Gets the UUID for the supplied username
     *
     * @param name the username to lookup, cannot be null
     *
     * @return the UUID, if found.
     *
     * @throws Exception thrown if something goes wrong
     */
    public static UUID getUUIDOf(String name) throws Exception {
        if (name == null) throw new IllegalArgumentException();

        if (Config.getInstance().getUseUUIDWebCache()) {
            if (UUID_PROVIDER == null) initProvider();
            PlayerRecord record = UUID_PROVIDER.doLookup(name);
            return record == null ? null : record.getUuid();
        } else return UUIDFetcher.getUUIDOf(name);
    }

    private static void initProvider() {
        UUID_PROVIDER = new CachingServiceProvider(new ApiV2Service());
    }

}
