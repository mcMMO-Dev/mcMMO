package com.gmail.nossr50.core;

import com.gmail.nossr50.core.adapters.NMS_114.BukkitPlatformAdapter;
import com.gmail.nossr50.core.adapters.PlatformAdapter;
import com.gmail.nossr50.mcMMO;

public class PlatformManager {
    private PlatformAdapter platformAdapter;
    private mcMMO pluginRef;

    public PlatformManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        initAdapters();
    }

    /**
     * Initialize the adapters based on the current platform
     */
    private void initAdapters() {
        pluginRef.getLogger().info("Initializing platform adapters...");
        //Determine which platform we are on and load the correct adapter
        //For now this will be hardcoded for testing purposes
        platformAdapter = new BukkitPlatformAdapter();
    }

    /**
     * Get the current platform adapter implementation
     * @return the current platform adapter
     */
    public PlatformAdapter getPlatformAdapter() {
        return platformAdapter;
    }

}
