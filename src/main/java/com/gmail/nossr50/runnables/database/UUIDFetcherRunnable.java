package com.gmail.nossr50.runnables.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.uuid.UUIDFetcher;

public class UUIDFetcherRunnable extends BukkitRunnable {
    private List<String> names;

    public UUIDFetcherRunnable(List<String> names) {
        this.names = names;
    }

    public UUIDFetcherRunnable(String name) {
        this.names = new ArrayList<String>();
        this.names.add(name);
    }

    @Override
    public void run() {
        try {
            Map<String, UUID> returns = new UUIDFetcher(this.names).call();
            new CacheReturnedNames(returns).runTask(mcMMO.p);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CacheReturnedNames extends BukkitRunnable {
        private Map<String, UUID> returns;

        public CacheReturnedNames(Map<String, UUID> returns) {
            this.returns = returns;
        }

        @Override
        public void run() {
            for (Entry<String, UUID> entry : this.returns.entrySet()) {
                mcMMO.getDatabaseManager().saveUserUUID(entry.getKey(), entry.getValue());
            }
        }
    }
}
