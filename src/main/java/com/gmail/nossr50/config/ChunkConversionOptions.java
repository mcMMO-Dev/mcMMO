package com.gmail.nossr50.config;

/**
 * This class is used to define settings for upgrading EXTREMELY OLD versions of mcMMO to newer versions
 * It could probably be deleted
 */
public class ChunkConversionOptions {
    private static final boolean chunkletsEnabled = true;
    private static final int conversionRate = 1;
    private static final boolean useEnchantmentBuffs = true;
    private static final int uuidConvertAmount = 5;
    private static final int mojangRateLimit = 50000;
    private static final long mojangLimitPeriod = 600000;

    public static boolean getChunkletsEnabled() {
        return chunkletsEnabled;
    }

    public static int getConversionRate() {
        return conversionRate;
    }

    public static boolean useEnchantmentBuffs() {
        return useEnchantmentBuffs;
    }

    public static int getUUIDConvertAmount() {
        return uuidConvertAmount;
    }

    public static int getMojangRateLimit() {
        return mojangRateLimit;
    }

    public static long getMojangLimitPeriod() {
        return mojangLimitPeriod;
    }
}
