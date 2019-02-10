package com.gmail.nossr50.core.datatypes.event;

/**
 * Different platforms have different event systems
 * These ENUMs will be the magic number for what kind of event we are targeting
 */
public enum EventType {
    //TODO: These are being based on the bukkit events mcMMO has used, the values will most likely change
    EVENT_BLOCK_PISTON_EXTEND,
    EVENT_BLOCK_PISTON_RETRACT,
    //Currently not sure I need this class, so I'll refrain from adding more events atm...

}
