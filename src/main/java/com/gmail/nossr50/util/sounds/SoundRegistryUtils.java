package com.gmail.nossr50.util.sounds;

import static java.lang.String.format;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.AttributeMapper;
import com.gmail.nossr50.util.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public final class SoundRegistryUtils {

    private static Method registryLookup;
    private static Object soundReg;

    public static final String PAPER_SOUND_REGISTRY_FIELD = "SOUND_EVENT";
    public static final String SPIGOT_SOUND_REGISTRY_FIELD = "SOUNDS";
    public static final String METHOD_GET_OR_THROW_NAME = "getOrThrow";
    public static final String METHOD_GET_NAME = "get";

    static {
        boolean foundRegistry = false;
        Class<?> registry;
        try {
            registry = Class.forName(AttributeMapper.ORG_BUKKIT_REGISTRY);
            try {
                // First check for Paper's sound registry, held by field SOUND_EVENT
                soundReg = registry.getField(PAPER_SOUND_REGISTRY_FIELD).get(null);
                foundRegistry = true;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                try {
                    soundReg = registry.getField(SPIGOT_SOUND_REGISTRY_FIELD);
                    foundRegistry = true;
                } catch (NoSuchFieldException ex) {
                    // ignored
                }
            }
        } catch (ClassNotFoundException e) {
            // ignored
        }

        if (foundRegistry) {
            try {
                // getOrThrow isn't in all API versions, but we use it if it exists
                registryLookup = soundReg.getClass().getMethod(METHOD_GET_OR_THROW_NAME,
                        NamespacedKey.class);
            } catch (NoSuchMethodException e) {
                try {
                    registryLookup = soundReg.getClass().getMethod(METHOD_GET_NAME,
                            NamespacedKey.class);
                } catch (NoSuchMethodException ex) {
                    // ignored exception
                    registryLookup = null;
                }
            }
        }
    }

    public static boolean useLegacyLookup() {
        return registryLookup == null;
    }

    public static @Nullable Sound getSound(String id, String fallBackId) {
        if (registryLookup != null) {
            try {
                return (Sound) registryLookup.invoke(soundReg, NamespacedKey.fromString(id));
            } catch(InvocationTargetException | IllegalAccessException
                    | IllegalArgumentException e) {
                if (fallBackId != null) {
                    LogUtils.debug(mcMMO.p.getLogger(),
                            format("Could not find sound with ID '%s', trying fallback ID '%s'", id,
                                    fallBackId));
                    try {
                        return (Sound) registryLookup.invoke(soundReg,
                                NamespacedKey.fromString(fallBackId));
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        mcMMO.p.getLogger().severe(format("Could not find sound with ID %s,"
                                + " fallback ID of %s also failed.", id, fallBackId));
                    }
                } else {
                    mcMMO.p.getLogger().severe(format("Could not find sound with ID %s.", id));
                }
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
