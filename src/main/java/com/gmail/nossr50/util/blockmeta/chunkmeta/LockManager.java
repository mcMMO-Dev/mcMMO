package com.gmail.nossr50.util.blockmeta.chunkmeta;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LockManager {

    private final Map<String, Object> keyLocks = new ConcurrentHashMap<>();

    private Object newLock(String key) {
        return keyLocks.computeIfAbsent(key, k -> new Object());
    }

    public CompletableFuture<Void> runAsyncWithLock(String key, Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            synchronized (newLock(key)) {
                runnable.run();
                keyLocks.remove(key);
            }
        });
    }

    public <U> CompletableFuture<U> supplyAsyncWithLock(String key, Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (newLock(key)) {
                U obj = supplier.get();
                keyLocks.remove(key);
                return obj;
            }
        });
    }
}
