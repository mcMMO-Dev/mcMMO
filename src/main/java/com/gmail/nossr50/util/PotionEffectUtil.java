package com.gmail.nossr50.util;

import org.bukkit.potion.PotionEffectType;

/**
 * This util class is responsible for mapping the correct potion effect types for the server version.
 * This is necessary because the potion effect types have changed between versions.
 * This util class will provide the correct potion effect types for the server version.
 */
final public class PotionEffectUtil {
    private static final PotionEffectType haste;
    private static final PotionEffectType nausea;

    static {
        haste = findHastePotionEffectType();
        nausea = findNauseaPotionEffectType();
    }

    private PotionEffectUtil() {
        // Utility class
    }

    private static PotionEffectType findNauseaPotionEffectType() {
        if (getNauseaLegacy() != null) {
            return getNauseaLegacy();
        } else {
            return getNauseaModern();
        }
    }

    private static PotionEffectType getNauseaModern() {
        try {
            return (PotionEffectType) PotionEffectType.class.getField("NAUSEA").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private static PotionEffectType getNauseaLegacy() {
        try {
            Object potionEffectTypeWrapper = PotionEffectType.class.getField("CONFUSION").get(null);
            return (PotionEffectType) potionEffectTypeWrapper;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }

    private static PotionEffectType findHastePotionEffectType() {
        if (getHasteLegacy() != null) {
            return getHasteLegacy();
        } else if (getHasteModern() != null) {
            return getHasteModern();
        } else {
            throw new IllegalStateException("Unable to find the Haste PotionEffectType");
        }
    }

    private static PotionEffectType getHasteLegacy() {
        try {
            Object potionEffectTypeWrapper = PotionEffectType.class.getField("FAST_DIGGING").get(null);
            return (PotionEffectType) potionEffectTypeWrapper;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }

    private static PotionEffectType getHasteModern() {
        try {
            return (PotionEffectType) PotionEffectType.class.getField("HASTE").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Get the Haste potion effect type.
     * This will return the correct potion effect type for the server version.
     * @return The Haste potion effect type.
     */
    public static PotionEffectType getHastePotionEffectType() {
        return haste;
    }

    /**
     * Get the Nausea potion effect type.
     * This will return the correct potion effect type for the server version.
     * @return The Nausea potion effect type.
     */
    public static PotionEffectType getNauseaPotionEffectType() {
        return nausea;
    }
}
