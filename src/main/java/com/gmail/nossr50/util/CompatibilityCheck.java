package com.gmail.nossr50.util;

import java.lang.reflect.Method;

public class CompatibilityCheck {
    /**
     * Uses reflection to check for incompatible server software
     */
    public static void checkForOutdatedAPI(boolean serverAPIOutdated, String software) {
        try {
            Class<?> checkForClass = Class.forName("org.bukkit.event.block.BlockDropItemEvent");
            Method newerAPIMethod = checkForClass.getMethod("getItems");
            Class<?> checkForClassBaseComponent = Class.forName("net.md_5.bungee.api.chat.BaseComponent");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            serverAPIOutdated = true;
            pluginRef.getLogger().severe("You are running an older version of " + software + " that is not compatible with mcMMO, update your server software!");
        }
    }
}
