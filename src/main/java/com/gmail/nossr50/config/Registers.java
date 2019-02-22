package com.gmail.nossr50.config;

/**
 * A class that is expected to register one thing into another thing
 */
public interface Registers extends Unload {
    /**
     * Register stuff
     */
    void register();
}
