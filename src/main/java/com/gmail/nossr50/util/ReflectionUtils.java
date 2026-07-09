package com.gmail.nossr50.util;

import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Small helpers for the version-compatibility shims that need reflection. Shims must degrade
 * gracefully: helpers return null on any failure instead of throwing, because several callers
 * run inside static initializers where a throw would poison the whole class.
 */
public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    /**
     * Reflectively invokes {@code className.valueOf(constantName)}, returning null when the
     * class, the valueOf method, or the constant does not exist on this server version.
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T staticValueOf(@NotNull String className,
            @NotNull String constantName) {
        try {
            final Class<?> clazz = Class.forName(className);
            final Method valueOf = clazz.getMethod("valueOf", String.class);
            return (T) valueOf.invoke(null, constantName);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            return null;
        }
    }
}
