package com.gmail.nossr50.listeners;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Pure analysis of a block's dropped materials deciding whether bonus drops can be trusted,
 * extracted from the block drop listener. Container blocks (tile entities) drop their contents
 * alongside themselves, and doubling those contents would dupe items, so drops that look like
 * they contain tile-entity spills are restricted or rejected.
 */
final class BonusDrops {

    /**
     * @param rewardable whether this break may receive bonus drops at all
     * @param onlyRewardBlocks whether bonus drops must be limited to block items because the
     * drop list looks like it contains tile-entity contents
     */
    record Analysis(boolean rewardable, boolean onlyRewardBlocks) {
    }

    private BonusDrops() {
    }

    /**
     * Analyzes the materials dropped by a broken block.
     *
     * @param brokenBlockType the type of the block that broke
     * @param droppedMaterials the materials of every dropped item entity
     */
    static @NotNull Analysis analyze(@NotNull Material brokenBlockType,
            @NotNull List<Material> droppedMaterials) {
        // beetroot drops two materials legitimately (beetroot + seeds); other plants may need
        // the same tolerance later
        final int tileEntityTolerance = brokenBlockType == Material.BEETROOTS ? 2 : 1;

        final Set<Material> uniqueMaterials = new HashSet<>(droppedMaterials);
        int blockCount = 0;
        for (Material material : droppedMaterials) {
            if (material.isBlock()) {
                blockCount++;
            }
        }

        // Too many distinct materials dropping - assume tile entity contents might be mixed in
        // and only reward blocks. Technically this also skips something like coal dropped when
        // a tile entity sat above a coal ore, but that is a rare edge case.
        final boolean onlyRewardBlocks = uniqueMaterials.size() > tileEntityTolerance;

        // More than one block in the drop list can't be trusted at all - back out entirely
        final boolean rewardable = blockCount <= 1;

        return new Analysis(rewardable, onlyRewardBlocks);
    }
}
