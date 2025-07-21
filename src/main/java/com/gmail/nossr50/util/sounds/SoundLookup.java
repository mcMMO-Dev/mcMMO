package com.gmail.nossr50.util.sounds;

import org.bukkit.NamespacedKey;
import java.lang.reflect.*;
import java.util.Locale;

public final class SoundLookup {

    private static final boolean USE_PAPER_REGISTRY;

    static {
        boolean paper = false;
        try { Class.forName("org.bukkit.Registry"); paper = true; } catch (ClassNotFoundException ignored) {}
        USE_PAPER_REGISTRY = paper;
    }

    public static boolean exists(String id) {
        if (USE_PAPER_REGISTRY) return paperRegistryExists(id);
        return legacyEnumExists(id);
    }

    private static boolean paperRegistryExists(String id) {
        try {
            Class<?> registry = Class.forName("org.bukkit.Registry");
            Object soundReg   = registry.getField("SOUND_EVENT").get(null);
            Method get        = soundReg.getClass().getMethod("get", NamespacedKey.class);
            return get.invoke(soundReg, NamespacedKey.fromString(id)) != null;
        } catch (Throwable t) { return false; }
    }

    private static boolean legacyEnumExists(String id) {
        String constant = toEnumName(id);
        try {
            Class<?> c = Class.forName("org.bukkit.Sound");
            if (c.isEnum()) {
                Enum.valueOf((Class<? extends Enum>) c, constant);
                return true;
            } else {
                c.getField(constant).get(null);
                return true;
            }
        } catch (Throwable t) { return false; }
    }

    private static String toEnumName(String id) {
        int i = id.indexOf(':');
        String core = i >= 0 ? id.substring(i + 1) : id;
        return core.replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
