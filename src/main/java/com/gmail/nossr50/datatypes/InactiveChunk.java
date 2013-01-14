package com.gmail.nossr50.datatypes;

import org.bukkit.Chunk;

public class InactiveChunk {
    public Chunk chunk;
    public int inactiveTime = 0;

    public InactiveChunk(Chunk chunk, int inactiveTime) {
        this.chunk = chunk;
        this.inactiveTime = inactiveTime;
    }

    public InactiveChunk(Chunk chunk) {
        this(chunk, 0);
    }
}