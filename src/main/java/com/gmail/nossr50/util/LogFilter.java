package com.gmail.nossr50.util;

import static com.gmail.nossr50.util.LogUtils.DEBUG_STR;

import com.gmail.nossr50.mcMMO;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {
    private final boolean debug;

    public LogFilter(mcMMO plugin) {
        // Doing a config loading lite here, because we can't depend on the config loader to have loaded before any debug messages are sent
        debug = plugin.getConfig().getBoolean("General.Verbose_Logging");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return !(record.getMessage().contains(DEBUG_STR) && !debug);
    }
}
