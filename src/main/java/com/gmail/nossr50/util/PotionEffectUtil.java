package com.gmail.nossr50.util;

import org.bukkit.potion.PotionEffectType;

/**
 * Provides the potion effect types mcMMO uses by their modern names.
 */
final public class PotionEffectUtil {

    private PotionEffectUtil() {
        // Utility class
    }

    /**
     * Get the Haste potion effect type.
     *
     * @return The Haste potion effect type.
     */
    public static PotionEffectType getHastePotionEffectType() {
        return PotionEffectType.HASTE;
    }

    /**
     * Get the Nausea potion effect type.
     *
     * @return The Nausea potion effect type.
     */
    public static PotionEffectType getNauseaPotionEffectType() {
        return PotionEffectType.NAUSEA;
    }
}
