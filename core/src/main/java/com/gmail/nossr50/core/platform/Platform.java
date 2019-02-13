package com.gmail.nossr50.core.platform;

/**
 * Represents the current API Platform
 * mcMMO supports multiple platforms, so that abstraction is handled through this interface
 */
public interface Platform {

    /**
     * Gets the name of the Platform
     *
     * @return name of this platform
     */
    String getPlatformName();

    /**
     * Gets the version of this platform
     *
     * @return the current version of this platform
     */
    String getPlatformVersion();

    /**
     * Gets the target version of Minecraft for this platform
     *
     * @return this platform's target minecraft version
     */
    String getTargetMinecraftVersion();

    /**
     * Whether or not this platform has been loaded
     *
     * @return true if the platform is loaded
     */
    boolean isPlatformLoaded();

    /**
     * Gets the PlatformSoftwareType for this platform
     *
     * @return this PlatformSoftwareType
     */
    PlatformSoftwareType getPlatformSoftwareType();
}
