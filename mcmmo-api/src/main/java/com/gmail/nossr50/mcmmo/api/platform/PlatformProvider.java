package com.gmail.nossr50.mcmmo.api.platform;

import com.gmail.nossr50.mcmmo.api.data.MMOPlayer;
import com.gmail.nossr50.mcmmo.api.platform.util.MetadataStore;

import java.io.File;
import java.util.logging.Logger;

public interface PlatformProvider {

    Logger getLogger();

    void tearDown();

    MetadataStore getMetadataStore();

    File getDataFolder();

    void getVersion();
}
