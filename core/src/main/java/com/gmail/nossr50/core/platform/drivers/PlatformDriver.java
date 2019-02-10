package com.gmail.nossr50.core.platform.drivers;

import com.gmail.nossr50.core.platform.Platform;

/**
 * Platform Drivers will handled translating our abstraction into instructions for various APIs
 */
public interface PlatformDriver {
    /**
     * Return the platform for this Driver
     * @return this platform
     */
    Platform getPlatform();

    /**
     * Gets the target MC Version for this driver
     * @return the target MC Version for this driver
     */
    String getTargetMinecraftVersion();
}
