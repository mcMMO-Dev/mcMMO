package com.gmail.nossr50.skills.bloodcraft;

public class Bloodcraft {
    /** Percent HP healed per rank (rank * LIFESTEAL_PERCENT_PER_RANK of damage dealt). */
    public static final double LIFESTEAL_PERCENT_PER_RANK = 0.02;

    /** HP threshold (fraction) below which Crimson Surge activates. */
    public static final double CRIMSON_SURGE_HP_THRESHOLD = 0.30;

    /** Bonus damage multiplier added per rank when HP is low. */
    public static final double CRIMSON_SURGE_BONUS_PER_RANK = 0.05;

    /** Additional rupture/bleed chance percent per rank granted by Rupture Mastery. */
    public static final double RUPTURE_MASTERY_BONUS_PER_RANK = 0.05;

    /** Duration of Berserker's Rush in ticks (8 seconds). */
    public static final int BERSERKERS_RUSH_DURATION_TICKS = 160;
}
