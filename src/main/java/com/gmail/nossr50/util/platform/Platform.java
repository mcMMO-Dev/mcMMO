package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.util.compat.CompatibilityManager;
import org.jetbrains.annotations.NotNull;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public interface Platform {

    /**
     * Target {@link ServerSoftwareType} for this {@link Platform}
     *
     * @return the {@link ServerSoftwareType} for this {@link Platform}
     */
    @NotNull ServerSoftwareType getServerSoftwareType();

    /**
     * Get the {@link CompatibilityManager} for this {@link Platform}
     *
     * @return the {@link CompatibilityManager} for this platform
     */
    @NotNull CompatibilityManager getCompatibilityManager();

    /**
     * The target game version of this {@link Platform}
     *
     * @return the target {@link MinecraftGameVersion} of this {@link Platform}
     */
    @NotNull MinecraftGameVersion getGameVersion();

}
