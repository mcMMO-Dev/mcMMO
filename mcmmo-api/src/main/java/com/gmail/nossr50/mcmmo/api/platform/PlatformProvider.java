package com.gmail.nossr50.mcmmo.api.platform;

import com.gmail.nossr50.mcmmo.api.platform.schedular.PlatformScheduler;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;

import java.io.File;
import java.util.logging.Logger;

public interface PlatformProvider {

    Logger getLogger();

    void tearDown();

    MetadataStore getMetadataStore();

    File getDataFolder();

    String getVersion();

    void earlyInit();

    boolean isSupported(boolean print);

    default boolean isSupported() {
        return isSupported(false);
    };

    ServerSoftwareType getServerType();

    void onLoad();

    void printUnsupported();

    PlatformScheduler getScheduler();
}
