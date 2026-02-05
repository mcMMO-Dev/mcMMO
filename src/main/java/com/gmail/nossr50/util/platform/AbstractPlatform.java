package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.util.compat.CompatibilityManager;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public abstract class AbstractPlatform implements Platform {

    protected final CompatibilityManager compatibilityManager;
    protected final MinecraftGameVersion minecraftGameVersion;
    protected final ServerSoftwareType serverSoftwareType;

    public AbstractPlatform(MinecraftGameVersion minecraftGameVersion,
            ServerSoftwareType serverSoftwareType, CompatibilityManager compatibilityManager) {
        this.minecraftGameVersion = minecraftGameVersion;
        this.serverSoftwareType = serverSoftwareType;
        this.compatibilityManager = compatibilityManager;
    }

}
