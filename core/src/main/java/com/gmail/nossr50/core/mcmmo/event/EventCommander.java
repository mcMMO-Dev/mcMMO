package com.gmail.nossr50.core.mcmmo.event;

public interface EventCommander {
    /**
     * Calls an event
     * @param event the event to call
     * @return the event after it has been passed around
     */
    Event callEvent(Event event);
}
