package com.gmail.nossr50.core.mcmmo.entity;

import com.gmail.nossr50.core.mcmmo.Location;
import com.gmail.nossr50.core.mcmmo.Named;

import java.util.UUID;

/**
 * Entities can be a lot of things in MC
 * Entities can be monsters, animals, players, etc...
 */
public interface Entity extends Location, Named {
    /**
     * The UUID for this entity
     * @return this entity's UUID
     */
    UUID getUUID();

    /**
     * The Location for this entity
     * @return this entity's location
     */
    Location getLocation();
}
