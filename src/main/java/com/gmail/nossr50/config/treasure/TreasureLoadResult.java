package com.gmail.nossr50.config.treasure;

/**
 * Outcome of attempting to load a single entry from a treasure config.
 *
 * <p>Treasure configs never fail server startup on a bad entry; each entry is classified into one
 * of the outcomes below, skipped when it cannot be used, and summarized in the logs.
 *
 * <ul>
 *   <li>{@link #LOADED} – the entry is valid and was registered.</li>
 *   <li>{@link #INCOMPATIBLE} – the entry references a material that does not exist in the running
 *       Minecraft version. This is harmless (for example an item shipped in a newer game version
 *       than the server is running) and is skipped quietly.</li>
 *   <li>{@link #INVALID} – the entry is misconfigured (bad field value, unknown rarity, malformed
 *       data, and so on). It is skipped and reported so an admin can find and fix it.</li>
 * </ul>
 */
enum TreasureLoadResult {
    LOADED,
    INCOMPATIBLE,
    INVALID
}
