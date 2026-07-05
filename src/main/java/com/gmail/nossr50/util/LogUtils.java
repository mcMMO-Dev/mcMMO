package com.gmail.nossr50.util;

import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class LogUtils {

    public static final String DEBUG_STR = "[D] ";

    public static void debug(@NotNull Logger logger, @NotNull String message) {
        // Messages here will get filtered based on config settings via LogFilter
        logger.info(DEBUG_STR + message);
    }
}
