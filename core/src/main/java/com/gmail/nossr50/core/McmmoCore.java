package com.gmail.nossr50.core;

import com.gmail.nossr50.core.mcmmo.event.EventCommander;
import com.gmail.nossr50.core.mcmmo.plugin.Plugin;
import com.gmail.nossr50.core.mcmmo.server.Server;

import java.util.logging.Logger;

public class McmmoCore {
    //TODO: Wire all this stuff
    public static Plugin p;
    private static EventCommander eventCommander;
    private static Server server;
    private static Logger logger;

    /**
     * Returns our Logger
     * @return the logger
     */
    public static Logger getLogger()
    {
        return logger;
    }

    public static EventCommander getEventCommander() {
        return eventCommander;
    }

    public static Server getServer() {
        return server;
    }
}
