package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Contains a snapshot of a block at a specific moment in time Used to check before/after type
 * stuff
 */
public class BlockSnapshot {
    private final Material oldType;
    private final Block blockRef;

    public BlockSnapshot(Material oldType, Block blockRef) {
        this.oldType = oldType;
        this.blockRef = blockRef;
    }

    public Material getOldType() {
        return oldType;
    }

    public Block getBlockRef() {
        return blockRef;
    }

    public boolean hasChangedType() {
        return oldType != blockRef.getState().getType();
    }
}
