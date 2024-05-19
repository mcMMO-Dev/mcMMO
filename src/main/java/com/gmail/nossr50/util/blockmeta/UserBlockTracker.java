package com.gmail.nossr50.util.blockmeta;

import com.gmail.nossr50.mcMMO;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Contains blockstore methods that are safe for external plugins to access.
 * An instance can be retrieved via {@link mcMMO#getUserBlockTracker() mcMMO.getPlaceStore()}
 */
public interface UserBlockTracker {
    /**
     * Check to see if a given {@link Block} is ineligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link Block} do not matter.
     *
     * @param block Block to check
     * @return true if the given block should not give rewards, false if otherwise
     */
    boolean isIneligible(@NotNull Block block);

    /**
     * Check to see if a given {@link Block} is eligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link Block} do not matter.
     *
     * @param block Block to check
     * @return true if the given block should give rewards, false if otherwise
     */
    boolean isEligible(@NotNull Block block);

    /**
     * Check to see if a given {@link BlockState} is eligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link BlockState} do not matter.
     *
     * @param blockState BlockState to check
     * @return true if the given BlockState location is set to true, false if otherwise
     */
    boolean isEligible(@NotNull BlockState blockState);

    /**
     * Check to see if a given {@link BlockState} is ineligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link BlockState} do not matter.
     *
     * @param blockState BlockState to check
     * @return true if the given BlockState location is set to true, false if otherwise
     */
    boolean isIneligible(@NotNull BlockState blockState);

    /**
     * Set a given {@link Block} as ineligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link Block} do not matter.
     *
     * @param block block whose location to set as ineligible
     */
    void setIneligible(@NotNull Block block);

    /**
     * Set a given BlockState location to true
     *
     * @param blockState BlockState location to set
     */
    void setIneligible(@NotNull BlockState blockState);

    /**
     * Set a given {@link Block} as eligible for rewards.
     * This is a location-based lookup, and the other properties of the {@link Block} do not matter.
     *
     * @param block block whose location to set as eligible
     */
    void setEligible(@NotNull Block block);

    /**
     * Set a given BlockState location to false
     *
     * @param blockState BlockState location to set
     */
    void setEligible(@NotNull BlockState blockState);

    /**
     * Check to see if a given block location is set to true
     *
     * @param block Block location to check
     * @return true if the given block location is set to true, false if otherwise
     * @deprecated Use {@link #isIneligible(Block)} instead
     */
    @Deprecated(since = "2.2.013")
    default boolean isTrue(@NotNull Block block) {
        return isIneligible(block);
    }

    /**
     * Check to see if a given BlockState location is set to true
     *
     * @param blockState BlockState to check
     * @return true if the given BlockState location is set to true, false if otherwise
     * @deprecated Use {@link #isIneligible(BlockState)} instead
     */
    @Deprecated(since = "2.2.013")
    default boolean isTrue(@NotNull BlockState blockState) {
        return isIneligible(blockState);
    }

    /**
     * Set a given block location to true
     *
     * @param block Block location to set
     * @deprecated Use {@link #setIneligible(Block)} instead
     */
    @Deprecated(since = "2.2.013")
    default void setTrue(@NotNull Block block) {
        setIneligible(block);
    }

    /**
     * Set a given BlockState location to true
     *
     * @param blockState BlockState location to set
     * @deprecated Use {@link #setIneligible(BlockState)} instead
     */
    @Deprecated(since = "2.2.013")
    default void setTrue(@NotNull BlockState blockState) {
        setIneligible(blockState);
    }

    /**
     * Set a given block location to false
     *
     * @param block Block location to set
     * @deprecated Use {@link #setEligible(Block)} instead
     */
    @Deprecated(since = "2.2.013")
    default void setFalse(@NotNull Block block) {
        setEligible(block);
    }

    /**
     * Set a given BlockState location to false
     *
     * @param blockState BlockState location to set
     * @deprecated Use {@link #setEligible(BlockState)} instead
     */
    @Deprecated(since = "2.2.013")
    default void setFalse(@NotNull BlockState blockState) {
        setEligible(blockState);
    }
}
