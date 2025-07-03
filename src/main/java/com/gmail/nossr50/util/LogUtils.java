package com.gmail.nossr50.util;

import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class LogUtils {

    public static final String DEBUG_STR = "[D] ";

    public static void debug(@NotNull Logger logger, @NotNull String message) {
        logger.info(DEBUG_STR + message);
    }
}
