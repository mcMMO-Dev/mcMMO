package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionUtil {
    // Some of the old potion types got renamed, our configs can still contain these old names
    private static final Map<String, String> legacyPotionTypes = new HashMap<>();
    private static final Method methodPotionTypeGetKey;
    private static final Method methodPotionTypeGetEffectType;
    private static final Method methodPotionTypeGetPotionEffects;
    private static final Method methodPotionDataIsUpgraded;
    private static final Method methodPotionDataIsExtended;
    private static final Method methodPotionDataGetType;
    private static final Method methodPotionMetaGetBasePotionData;
    private static final Method methodPotionMetaGetBasePotionType;
    private static final Method methodPotionMetaSetBasePotionType;
    private static final Method methodSetBasePotionData;
    private static final Class<?> potionDataClass;

    public static final String STRONG = "STRONG";
    public static final String LONG = "LONG";
    public static final String WATER_POTION_TYPE_STR = "WATER";

    private static final PotionCompatibilityType COMPATIBILITY_MODE;

    static {
        potionDataClass = getPotionDataClass();
        // Uncraftable doesn't exist in modern versions
        // It served as a potion that didn't craft into anything else so it didn't conflict with vanilla systems
        // Instead we will use Mundane, which doesn't make anything in vanilla systems
        legacyPotionTypes.put("UNCRAFTABLE", "MUNDANE");
        legacyPotionTypes.put("JUMP", "LEAPING");
        legacyPotionTypes.put("SPEED", "SWIFTNESS");
        legacyPotionTypes.put("INSTANT_HEAL", "HEALING");
        legacyPotionTypes.put("INSTANT_DAMAGE", "HARMING");
        legacyPotionTypes.put("REGEN", "REGENERATION");
        methodPotionTypeGetKey = getKeyMethod();
        methodPotionDataIsUpgraded = getPotionDataIsUpgraded();
        methodPotionDataIsExtended = getIsExtended();
        methodPotionMetaGetBasePotionData = getBasePotionData();
        methodPotionMetaGetBasePotionType = getBasePotionType();
        methodPotionMetaSetBasePotionType = getMethodPotionMetaSetBasePotionType();
        methodPotionDataGetType = getPotionDataGetType();
        methodPotionTypeGetEffectType = getPotionTypeEffectType();
        methodPotionTypeGetPotionEffects = getPotionTypeGetPotionEffects();
        methodSetBasePotionData = getSetBasePotionData();

        if (potionDataClass != null) {
            COMPATIBILITY_MODE = PotionCompatibilityType.PRE_1_20_5;
        } else {
            COMPATIBILITY_MODE = PotionCompatibilityType.POST_1_20_5;
        }
    }

    /**
     * Derive a potion from a partial name, and whether it should be upgraded or extended.
     * @param partialName potion type as a string, can be a substring of the potion type but must match exactly
     * @return The potion type
     */
    public static PotionType matchPotionType(String partialName, boolean isUpgraded, boolean isExtended) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return matchLegacyPotionType(partialName);
        } else {
            String updatedName = convertLegacyNames(partialName);
            return Arrays.stream(PotionType.values())
                    .filter(potionType -> getKeyGetKey(potionType).toUpperCase().contains(partialName)
                                || getKeyGetKey(potionType).toUpperCase().contains(convertLegacyNames(updatedName)))
                    .filter(potionType -> !isUpgraded || potionType.name().toUpperCase().contains(STRONG))
                    .filter(potionType -> !isExtended || potionType.name().toUpperCase().contains(LONG))
                    .findAny().orElse(null);
        }
    }

    public static String getKeyGetKey(PotionType potionType) {
        try {
            if (getKeyMethod() != null) {
                NamespacedKey key = (NamespacedKey) methodPotionTypeGetKey.invoke(potionType);
                return key.getKey();
            } else {
                return potionType.name();
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            mcMMO.p.getLogger().warning("Failed to get potion key for " + potionType.name());
            return potionType.name();
        }
    }

    private static Class<?> getPotionDataClass() {
        try {
            return Class.forName("org.bukkit.potion.PotionData");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Older versions of Spigot do not have getKey() in PotionType
     * We need to check for the existence of this method before calling it
     * @return The getKey method
     */
    private static @Nullable Method getKeyMethod() {
        try {
            return PotionType.class.getMethod("getKey");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getMethodPotionMetaSetBasePotionType() {
        try {
            return PotionMeta.class.getMethod("setBasePotionType", PotionType.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getSetBasePotionData() {
        try {
            return PotionMeta.class.getMethod("setBasePotionData", potionDataClass);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static @Nullable Method getPotionDataIsUpgraded() {
        try {
            // TODO: <?> Needed?
            final Class<?> clazz = Class.forName("org.bukkit.potion.PotionData");
            return clazz.getMethod("isUpgraded");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            return null;
        }
    }

    private static @Nullable Method getIsExtended() {
        try {
            // TODO: <?> Needed?
            final Class<?> clazz = Class.forName("org.bukkit.potion.PotionData");
            return clazz.getMethod("isExtended");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Newer versions of Spigot do not have getBasePotionData() in PotionMeta
     *
     * @return the getBasePotionData method, or null if it does not exist
     */
    private static @Nullable Method getBasePotionData() {
        try {
            return PotionMeta.class.getMethod("getBasePotionData");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getBasePotionType() {
        try {
            return PotionMeta.class.getMethod("getBasePotionType");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getPotionDataGetType() {
        try {
            final Class<?> clazz = Class.forName("org.bukkit.potion.PotionData");
            return clazz.getMethod("getType");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            return null;
        }
    }

    private static Method getPotionTypeEffectType() {
        try {
            return PotionType.class.getMethod("getEffectType");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getPotionTypeGetPotionEffects() {
        try {
            return PotionType.class.getMethod("getPotionEffects");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Legacy matching for {@link PotionType}
     *
     * @param partialName The partial name of the potion
     * @return The potion type
     */
    private static PotionType matchLegacyPotionType(String partialName) {
        String updatedName = convertLegacyNames(partialName);

        return Arrays.stream(PotionType.values())
                .filter(potionType -> getKeyGetKey(potionType).equalsIgnoreCase(partialName)
                        || getKeyGetKey(potionType).equalsIgnoreCase(convertLegacyNames(updatedName))
                        || potionType.name().equalsIgnoreCase(partialName)
                        || potionType.name().equalsIgnoreCase(convertLegacyNames(updatedName)))
                .findAny().orElse(null);
    }

    public static String convertPotionConfigName(String legacyName) {
        String replacementName = legacyName;

        // Remove generated potions.yml config naming convention
        if (replacementName.contains("POTION_OF_")) {
            replacementName = replacementName.replace("POTION_OF_", "");
        }

        if (replacementName.contains("_II")) {
            replacementName = replacementName.replace("_II", "");
            replacementName = "STRONG_" + replacementName;
        } else if (replacementName.contains("_EXTENDED")) {
            replacementName = replacementName.replace("_EXTENDED", "");
            replacementName = "LONG_" + replacementName;
        }
        return replacementName;
    }

    public static String convertLegacyNames(String legacyPotionType) {
        String modernized = legacyPotionType;
        // check for legacy names
        for (var key : legacyPotionTypes.keySet()) {
            if (modernized.contains(key)) {
                // Replace the legacy name with the new name
                modernized = modernized.replace(key, legacyPotionTypes.get(key));
                break;
            }
        }
        return modernized;
    }

    public static boolean hasLegacyName(String potionType) {
        for (var key : legacyPotionTypes.keySet()) {
            if (potionType.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStrong(PotionMeta potionMeta) {
        if (methodPotionMetaGetBasePotionData == null) {
            return isStrongModern(potionMeta);
        } else {
            return isStrongLegacy(potionMeta);
        }

    }

    public static boolean isLong(PotionMeta potionMeta) {
        if (methodPotionMetaGetBasePotionData == null) {
            return isLongModern(potionMeta);
        } else {
            return isLongLegacy(potionMeta);
        }
    }

    private static boolean isLongLegacy(PotionMeta potionMeta) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            return (boolean) methodPotionDataIsExtended.invoke(potionData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isLongModern(PotionMeta potionMeta) {
        try {
            return getModernPotionTypeKey(potionMeta).getKey().startsWith(LONG);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isStrongLegacy(PotionMeta potionMeta) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            return (boolean) methodPotionDataIsUpgraded.invoke(potionData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isStrongModern(PotionMeta potionMeta) {
        try {
            return getModernPotionTypeKey(potionMeta).getKey().startsWith(STRONG);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static NamespacedKey getModernPotionTypeKey(PotionMeta potionMeta) throws IllegalAccessException, InvocationTargetException {
        PotionType potionType = (PotionType) methodPotionMetaGetBasePotionType.invoke(potionMeta);
        return (NamespacedKey) methodPotionTypeGetKey.invoke(potionType);
    }

    public static boolean isPotionJustWater(PotionMeta potionMeta) {
        return isPotionTypeWater(potionMeta)
                && !hasBasePotionEffects(potionMeta)
                && potionMeta.getCustomEffects().isEmpty();
    }

    public static boolean isPotionTypeWater(@NotNull PotionMeta potionMeta) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return isPotionTypeWaterLegacy(potionMeta);
        } else {
            return isPotionTypeWaterModern(potionMeta);
        }
    }

    public static boolean isPotionType(@NotNull PotionMeta potionMeta, String potionType) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return isPotionTypeLegacy(potionMeta, potionType);
        } else {
            return isPotionTypeModern(potionMeta, potionType);
        }
    }

    public static boolean isPotionTypeWithoutEffects(@NotNull PotionMeta potionMeta, String potionType) {
        return isPotionType(potionMeta, potionType)
                && !hasBasePotionEffects(potionMeta)
                && potionMeta.getCustomEffects().isEmpty();
    }

    private static boolean isPotionTypeModern(@NotNull PotionMeta potionMeta, String potionType) {
        try {
            return getModernPotionTypeKey(potionMeta).getKey().equalsIgnoreCase(potionType);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isPotionTypeLegacy(@NotNull PotionMeta potionMeta, String potionType) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            PotionType potionTypeObj = (PotionType) methodPotionDataGetType.invoke(potionData);
            return potionTypeObj.name().equalsIgnoreCase(potionType);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isPotionTypeWaterLegacy(@NotNull PotionMeta potionMeta) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            PotionType potionType = (PotionType) methodPotionDataGetType.invoke(potionData);
            return potionType.name().equalsIgnoreCase(WATER_POTION_TYPE_STR)
                    || PotionType.valueOf(WATER_POTION_TYPE_STR) == potionType;
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isPotionTypeWaterModern(@NotNull PotionMeta potionMeta) {
        try {
            return getModernPotionTypeKey(potionMeta).getKey().equalsIgnoreCase(WATER_POTION_TYPE_STR);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean samePotionType(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return samePotionTypeLegacy(potionMeta, otherPotionMeta);
        } else {
            return samePotionTypeModern(potionMeta, otherPotionMeta);
        }
    }

    private static boolean samePotionTypeLegacy(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            Object otherPotionData = methodPotionMetaGetBasePotionData.invoke(otherPotionMeta);
            PotionType potionType = (PotionType) methodPotionDataGetType.invoke(potionData);
            PotionType otherPotionType = (PotionType) methodPotionDataGetType.invoke(otherPotionData);
            return potionType == otherPotionType;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean samePotionTypeModern(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        try {
            PotionType potionType = (PotionType) methodPotionMetaGetBasePotionType.invoke(potionMeta);
            PotionType otherPotionType = (PotionType) methodPotionMetaGetBasePotionType.invoke(otherPotionMeta);
            return potionType == otherPotionType;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean samePotionEffects(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return true;
        } else {
            return samePotionEffectsModern(potionMeta, otherPotionMeta);
        }
    }

    private static boolean samePotionEffectsModern(PotionMeta potionMeta, PotionMeta otherPotionMeta) {
        return potionMeta.getCustomEffects().equals(otherPotionMeta.getCustomEffects());
    }

    public static boolean hasBasePotionEffects(PotionMeta potionMeta) {
        if (COMPATIBILITY_MODE == PotionCompatibilityType.PRE_1_20_5) {
            return hasBasePotionEffectsLegacy(potionMeta);
        } else {
            return hasBasePotionEffectsModern(potionMeta);
        }
    }

    private static boolean hasBasePotionEffectsLegacy(PotionMeta potionMeta) {
        try {
            Object potionData = methodPotionMetaGetBasePotionData.invoke(potionMeta);
            PotionType potionType = (PotionType) methodPotionDataGetType.invoke(potionData);
            return methodPotionTypeGetEffectType.invoke(potionType) != null;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean hasBasePotionEffectsModern(PotionMeta potionMeta) {
        try {
            PotionType potionType = (PotionType) methodPotionMetaGetBasePotionType.invoke(potionMeta);
            List<PotionEffectType> potionEffectTypeList = (List<PotionEffectType>) methodPotionTypeGetPotionEffects.invoke(potionType);
            return potionEffectTypeList != null && !potionEffectTypeList.isEmpty();
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Set the base potion type of a potion meta.
     * Note that extended/upgraded are ignored in 1.20.5 and later.
     *
     * @param potionMeta the potion meta
     * @param extended true if the potion is extended
     * @param upgraded true if the potion is upgraded
     */
    public static void setBasePotionType(PotionMeta potionMeta, PotionType potionType, boolean extended, boolean upgraded) {
        if (methodPotionMetaSetBasePotionType == null) {
            setBasePotionTypeLegacy(potionMeta, potionType, extended, upgraded);
        } else {
            setBasePotionTypeModern(potionMeta, potionType);
        }
    }

    private static void setBasePotionTypeLegacy(PotionMeta potionMeta, PotionType potionType, boolean extended,
                                                boolean upgraded) {
        try {
            Object potionData = potionDataClass.getConstructor(PotionType.class, boolean.class, boolean.class)
                    .newInstance(potionType, extended, upgraded);
            methodSetBasePotionData.invoke(potionMeta, potionData);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setBasePotionTypeModern(PotionMeta potionMeta, PotionType potionType) {
        try {
            methodPotionMetaSetBasePotionType.invoke(potionMeta, potionType);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}
