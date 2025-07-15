package com.gmail.nossr50.datatypes.database;

import org.jetbrains.annotations.NotNull;

public record PlayerStat(String playerName, int value) implements Comparable<PlayerStat> {
    @Override
    public int compareTo(@NotNull PlayerStat o) {
        // Descending order
        int cmp = Integer.compare(o.value, this.value);
        if (cmp != 0) return cmp;
        // Tie-breaker
        return this.playerName.compareTo(o.playerName);
    }
}
