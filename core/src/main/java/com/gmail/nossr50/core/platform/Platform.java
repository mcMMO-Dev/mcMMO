package com.gmail.nossr50.core.platform;

import com.gmail.nossr50.core.mcmmo.server.Server;
import com.gmail.nossr50.core.mcmmo.tasks.TaskScheduler;

import java.io.File;

/**
 * Represents the current API Platform
 * mcMMO supports multiple platforms, so that abstraction is handled through this interface
 */
public interface Platform {

    /**
     * Gets the MC Server implementation for this Platform
     * @return the MC server object
     */
    Server getServer();

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

    /**
     * Gets the task Scheduler
     * @return the task scheduler
     */
    TaskScheduler getScheduler();


    /**
     * Gets a resource stream from inside the JAR at a specified path
     * @param path the path inside the JAR where the resource stream is found
     * @return the resource stream
     */
    java.io.InputStream getResource(String path);

    /**
     * Gets the path of the Data folder for this platform
     * @return this platform's data folder
     */
    File getDataFolderPath();
}
