package com.gmail.nossr50.core.mcmmo.entity;

import com.gmail.nossr50.core.mcmmo.Named;
import com.gmail.nossr50.core.mcmmo.Unique;
import com.gmail.nossr50.core.mcmmo.meta.MetadataHolder;
import com.gmail.nossr50.core.mcmmo.world.Location;

/**
 * Entities can be a lot of things in MC
 * Entities can be monsters, animals, players, etc...
 */
public interface Entity extends Location, Named, Unique, MetadataHolder {

    /**
     * The Location for this entity
     *
     * @return this entity's location
     */
    Location getLocation();
}
