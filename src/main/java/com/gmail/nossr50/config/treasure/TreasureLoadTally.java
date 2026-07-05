package com.gmail.nossr50.config.treasure;

/**
 * Running counts of treasure entries by outcome, used to aggregate results across one or more
 * treasure sections into a single load summary. This keeps startup logging quiet: for example all
 * of the per-entity {@code Shake.*} sections are summarized in one line rather than one line each.
 */
record TreasureLoadTally(int loaded, int incompatible, int invalid) {

    static TreasureLoadTally empty() {
        return new TreasureLoadTally(0, 0, 0);
    }

    TreasureLoadTally merge(final TreasureLoadTally other) {
        return new TreasureLoadTally(loaded + other.loaded, incompatible + other.incompatible,
                invalid + other.invalid);
    }

    /** Number of entries that were expected to load: those that loaded plus those that were invalid. */
    int attempted() {
        return loaded + invalid;
    }
}
