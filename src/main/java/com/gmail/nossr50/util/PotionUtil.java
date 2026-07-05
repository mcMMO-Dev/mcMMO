package com.gmail.nossr50.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PotionUtil {
    // Some of the old potion types got renamed, our configs can still contain these old names
    private static final Map<String, String> legacyPotionTypes = new HashMap<>();

    public static final String STRONG = "STRONG";
    public static final String LONG = "LONG";

    static {
        // Uncraftable doesn't exist in modern versions
        // It served as a potion that didn't craft into anything else so it didn't conflict with vanilla systems
        // Instead we will use Mundane, which doesn't make anything in vanilla systems
        legacyPotionTypes.put("UNCRAFTABLE", "MUNDANE");
        legacyPotionTypes.put("JUMP", "LEAPING");
        legacyPotionTypes.put("SPEED", "SWIFTNESS");
        legacyPotionTypes.put("INSTANT_HEAL", "HEALING");
        legacyPotionTypes.put("INSTANT_DAMAGE", "HARMING");
        legacyPotionTypes.put("REGEN", "REGENERATION");
    }

    private PotionUtil() {}

    /**
     * Derive a potion from a partial name, and whether it should be upgraded or extended.
     *
     * <p>In 1.20.5+, "upgraded" and "extended" are represented by distinct {@link PotionType}
     * constants (e.g. STRONG_HEALING, LONG_SWIFTNESS), not flags.
     *
     * @param partialName potion type as a string, can be a substring of the potion type but must match
     *                    against the potion's key or enum name
     * @return The potion type, or null if no match
     */
    public static @Nullable PotionType matchPotionType(String partialName, boolean isUpgraded, boolean isExtended) {
        if (partialName == null || partialName.isEmpty()) {
            return null;
        }

        final String updatedName = convertLegacyNames(partialName).toUpperCase(Locale.ENGLISH);

        return Arrays.stream(PotionType.values())
                // Allow matching by namespace key ("swiftness", "long_swiftness") or enum name
                .filter(potionType -> {
                    final NamespacedKey key = potionType.getKey();
                    final String keyStr = key != null ? key.getKey() : "";
                    return keyStr.toUpperCase(Locale.ENGLISH).contains(updatedName)
                            || potionType.name().toUpperCase(Locale.ENGLISH).contains(updatedName);
                })
                // Enforce strong/long selection by the enum name prefix convention
                .filter(potionType -> isUpgraded == potionType.name().startsWith(STRONG + "_"))
                .filter(potionType -> isExtended == potionType.name().startsWith(LONG + "_"))
                .findAny()
                .orElse(null);
    }

    /**
     * Returns the NamespacedKey key string portion for this potion type (e.g. "swiftness").
     */
    public static @NotNull String getKeyGetKey(@NotNull PotionType potionType) {
        final NamespacedKey key = potionType.getKey();
        return key != null ? key.getKey() : potionType.name();
    }

    public static String convertPotionConfigName(String legacyName) {
        String replacementName = legacyName;

        // Remove generated potions.yml config naming convention
        if (replacementName.contains("POTION_OF_")) {
            replacementName = replacementName.replace("POTION_OF_", "");
        }

        if (replacementName.contains("_II")) {
            replacementName = replacementName.replace("_II", "");
            replacementName = STRONG + "_" + replacementName;
        } else if (replacementName.contains("_EXTENDED")) {
            replacementName = replacementName.replace("_EXTENDED", "");
            replacementName = LONG + "_" + replacementName;
        }

        return replacementName;
    }

    public static String convertLegacyNames(String legacyPotionType) {
        String modernized = legacyPotionType;

        for (var key : legacyPotionTypes.keySet()) {
            if (modernized.contains(key)) {
                modernized = modernized.replace(key, legacyPotionTypes.get(key));
                break;
            }
        }

        return modernized;
    }

    public static boolean isStrong(@NotNull PotionMeta potionMeta) {
        final PotionType base = potionMeta.getBasePotionType();
        return base != null && base.name().startsWith(STRONG + "_");
    }

    public static boolean isLong(@NotNull PotionMeta potionMeta) {
        final PotionType base = potionMeta.getBasePotionType();
        return base != null && base.name().startsWith(LONG + "_");
    }

    public static boolean isPotionTypeWater(@NotNull PotionMeta potionMeta) {
        return potionMeta.getBasePotionType() == PotionType.WATER;
    }

    public static boolean hasBasePotionEffects(@NotNull PotionMeta potionMeta) {
        final PotionType base = potionMeta.getBasePotionType();
        if (base == null) {
            return false;
        }

        final List<PotionEffect> effects = base.getPotionEffects();
        return effects != null && !effects.isEmpty();
    }

    /**
     * Set the base potion type of a potion meta. Note that extended/upgraded are ignored in 1.20.5
     * and later.
     *
     * <p>In 1.20.5+, "extended/upgraded" are encoded into {@link PotionType} variants. This method
     * attempts to select the appropriate STRONG_/LONG_ variant when requested. If no such variant
     * exists, it falls back to the provided base type.</p>
     */
    public static void setBasePotionType(@NotNull PotionMeta potionMeta,
            @NotNull PotionType potionType, boolean extended, boolean upgraded) {
        final PotionType resolved = resolveVariant(potionType, upgraded, extended);
        potionMeta.setBasePotionType(resolved);
    }

    private static @NotNull PotionType resolveVariant(@NotNull PotionType base, boolean upgraded,
            boolean extended) {
        // Apply the same prefix scheme your code already expects
        String name = base.name();

        // Avoid double-prefixing if caller already passed LONG_/STRONG_ variants
        if (name.startsWith(STRONG + "_")) {
            upgraded = false;
        }
        if (name.startsWith(LONG + "_")) {
            extended = false;
        }

        if (upgraded) {
            name = STRONG + "_" + name;
        }
        if (extended) {
            name = LONG + "_" + name;
        }

        try {
            return PotionType.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            // Not all potion types have strong/long variants; just use the provided base.
            return base;
        }
    }
}
