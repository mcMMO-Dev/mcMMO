package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final public class PotionEffectMapper {
    private static final PotionEffectType haste;
    private static final PotionEffectType nausea;
    private static final Method potionEffectTypeWrapperGetPotionEffectType;
    private static final Class<?> classPotionEffectTypeWrapper;

    private PotionEffectMapper() {
        // Utility class
    }

    static {
        potionEffectTypeWrapperGetPotionEffectType = getPotionEffectTypeWrapperGetPotionEffectType();
        classPotionEffectTypeWrapper = getClassPotionEffectTypeWrapper();
        
        haste = initHaste();
        nausea = initNausea();
    }

    private static Method getPotionEffectTypeWrapperGetPotionEffectType() {
        try {
            return classPotionEffectTypeWrapper.getMethod("getType");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Class<?> getClassPotionEffectTypeWrapper() {
        try {
            return Class.forName("org.bukkit.potion.PotionEffectTypeWrapper");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static PotionEffectType initNausea() {
        if (classPotionEffectTypeWrapper != null) {
            return getNauseaLegacy();
        } else {
            return getNauseaModern();
        }
    }

    private static PotionEffectType getNauseaModern() {
//        PotionEffectType potionEffectType = Registry.EFFECT.match("nausea");
//        if (potionEffectType != null) {
//            return potionEffectType;
//        }
//
//        // Look for the potion effect type by name
//        for (PotionEffectType pet : Registry.EFFECT) {
//            if (pet.getKey().getKey().equalsIgnoreCase("CONFUSION")
//                    || pet.getKey().getKey().equalsIgnoreCase("NAUSEA")
//                    || pet.getName().equalsIgnoreCase("CONFUSION")
//                    || pet.getName().equalsIgnoreCase("NAUSEA")) {
//                return pet;
//            }
//        }

        try {
            return (PotionEffectType) PotionEffectType.class.getField("NAUSEA").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            mcMMO.p.getLogger().severe("Unable to find the Nausea potion effect type, " +
                    "mcMMO will not function properly.");
            throw new IllegalStateException("Unable to find the Nausea potion effect type");
        }
    }

    private static PotionEffectType getNauseaLegacy() {
        try {
            Object potionEffectTypeWrapper = PotionEffectType.class.getField("CONFUSION").get(null);
            PotionEffectType potionEffectType = (PotionEffectType) potionEffectTypeWrapperGetPotionEffectType
                    .invoke(potionEffectTypeWrapper);
            return potionEffectType;
        } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
            mcMMO.p.getLogger().severe("Unable to find the Nausea potion effect type, " +
                    "mcMMO will not function properly.");
            throw new IllegalStateException("Unable to find the Nausea potion effect type");
        }
    }

    private static PotionEffectType initHaste() {


        mcMMO.p.getLogger().severe("Unable to find the Haste potion effect type, " +
                "mcMMO will not function properly.");
        throw new IllegalStateException("Unable to find the Haste potion effect type");
    }

    private static PotionEffectType getHasteLegacy() {
        try {
            Object potionEffectTypeWrapper = PotionEffectType.class.getField("FAST_DIGGING").get(null);
            PotionEffectType potionEffectType = (PotionEffectType) potionEffectTypeWrapperGetPotionEffectType
                    .invoke(potionEffectTypeWrapper);
            return potionEffectType;
        } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
            mcMMO.p.getLogger().severe("Unable to find the Haste potion effect type, " +
                    "mcMMO will not function properly.");
            throw new IllegalStateException("Unable to find the Haste potion effect type");
        }
    }

    private static PotionEffectType getHasteModern() {
//        PotionEffectType potionEffectType = Registry.EFFECT.match("haste");
//        if (potionEffectType != null) {
//            return potionEffectType;
//        }
//
//        // Look for the potion effect type by name
//        for (PotionEffectType pet : Registry.EFFECT) {
//            if (pet.getKey().getKey().equalsIgnoreCase("HASTE")
//                    || pet.getKey().getKey().equalsIgnoreCase("FAST_DIGGING")
//                    || pet.getName().equalsIgnoreCase("HASTE")
//                    || pet.getName().equalsIgnoreCase("FAST_DIGGING")) {
//                return pet;
//            }
//        }

        try {
            return (PotionEffectType) PotionEffectType.class.getField("HASTE").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            mcMMO.p.getLogger().severe("Unable to find the Haste potion effect type, " +
                    "mcMMO will not function properly.");
            throw new IllegalStateException("Unable to find the Haste potion effect type");
        }
    }

    public static PotionEffectType getHaste() {
        return haste;
    }

    public static PotionEffectType getNausea() {
        return nausea;
    }
}
