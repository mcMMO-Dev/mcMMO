package com.gmail.nossr50.mcmmo.api.platform;

import java.util.logging.Logger;

public interface PlatformProvider {

    Logger getLogger();

    void tearDown();
}
