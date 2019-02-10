package com.gmail.nossr50.core.mcmmo.event;

/**
 * This class handles cancellations for an event
 */
public interface Cancellable {

    /**
     * Whether or not the event is cancelled
     *
     * @return true if cancelled
     */
    Boolean isCancelled();

    /**
     * Sets an events cancellation to b
     *
     * @param b
     */
    void setCancelled(boolean b);
}
